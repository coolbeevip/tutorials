package org.coolbeevip.arrow.labs;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ArrowBuf;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.BufferLayout.BufferType;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.TypeLayout;
import org.apache.arrow.vector.VarBinaryVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.util.Text;

/**
 * @author zhanglei
 */
public class ArrowWriter {

  private boolean useNullValues;
  private int batchSize;
  private VectorSchemaRoot root;
  private RootAllocator rootAllocator;
  private ArrowFileWriter arrowFileWriter;

  private Schema schema(){
    // 定义数据结构
    ImmutableList.Builder<Field> childrenBuilder = ImmutableList.builder();
    // 32位有符号整数
    childrenBuilder.add(new Field("int", FieldType.nullable(new ArrowType.Int(32, true)), null));
    // 42位有符号整数
    childrenBuilder.add(new Field("long", FieldType.nullable(new ArrowType.Int(64, true)), null));
    // 字节数组
    childrenBuilder.add(new Field("binary", FieldType.nullable(ArrowType.Utf8.INSTANCE), null));
    // 有符号单精度浮点
    childrenBuilder
        .add(new Field("float", FieldType.nullable(new ArrowType.FloatingPoint(SINGLE)), null));
    // 有符号双精度浮点
    childrenBuilder
        .add(new Field("double", FieldType.nullable(new ArrowType.FloatingPoint(DOUBLE)), null));
    // 字符串
    childrenBuilder.add(new Field("string", FieldType.nullable(new ArrowType.Binary()), null));
    return new Schema(childrenBuilder.build(), null);
  };

  public ArrowWriter(FileOutputStream outputStream, int batchSize, boolean useNullValues, long allocatorLimit) {
    this.batchSize = batchSize;
    this.useNullValues = useNullValues;
    // 定义内存分配
    this.rootAllocator = new RootAllocator(allocatorLimit);
    // 创建数据操作入口
    this.root = VectorSchemaRoot.create(schema(), this.rootAllocator);
    // 创建一个编码器
    DictionaryProvider.MapDictionaryProvider provider = new DictionaryProvider.MapDictionaryProvider();
    // 创建写文件对象
    this.arrowFileWriter = new ArrowFileWriter(root, provider, outputStream.getChannel());
    showSchema();
  }

  private void showSchema() {
    System.out.println("schema is "  + root.getSchema().toString());
    for (Field field : root.getSchema().getFields()) {
      FieldVector vector = root.getVector(field.getName());
      showFieldLayout(field, vector);
    }
  }

  public void writeArrow(Data[] data) throws Exception {
    try {
      arrowFileWriter.start();
      for (int i = 0; i < data.length; ) {
        int toProcessItems = Math.min(this.batchSize, data.length - i);
        root.setRowCount(toProcessItems);
        for (Field field : root.getSchema().getFields()) {
          System.out.println("写入列["+field.getName()+"] "+i+".."+(i+toProcessItems-1));
          FieldVector vector = root.getVector(field.getName());
          switch (vector.getMinorType()) {
            case INT:
              writeFieldInt(data, vector, i, toProcessItems);
              break;
            case BIGINT:
              writeFieldLong(data, vector, i, toProcessItems);
              break;
            case VARBINARY:
              writeFieldVarBinary(data, vector, i, toProcessItems);
              break;
            case FLOAT4:
              writeFieldFloat4(data, vector, i, toProcessItems);
              break;
            case FLOAT8:
              writeFieldFloat8(data, vector, i, toProcessItems);
              break;
            case VARCHAR:
              writeFieldVarChar(data, vector, i, toProcessItems);
              break;
            default:
              throw new Exception(" Not supported yet type: " + vector.getMinorType());
          }
        }
        arrowFileWriter.writeBatch();
        i += toProcessItems;
      }
    } finally {
      arrowFileWriter.end();
    }
  }

  protected void closeArrow(){
    arrowFileWriter.close();
  }

  private int isSet() {
//    if (useNullValues) {
//      return 0;
//    }
    return 1;
  }

  protected void writeFieldInt(Data[] data, FieldVector fieldVector, int from, int items) {
    IntVector intVector = (IntVector) fieldVector;
    intVector.setInitialCapacity(items);
    intVector.allocateNew();
    for (int i = 0; i < items; i++) {
      intVector.setSafe(i, isSet(), data[from + i].anInt);
    }
    fieldVector.setValueCount(items);

  }

  protected void writeFieldLong(Data[] data, FieldVector fieldVector, int from, int items) {
    BigIntVector bigIntVector = (BigIntVector) fieldVector;
    bigIntVector.setInitialCapacity(items);
    bigIntVector.allocateNew();
    for (int i = 0; i < items; i++) {
      bigIntVector.setSafe(i, isSet(), data[from + i].aLong);
    }
    bigIntVector.setValueCount(items);
  }

  protected void writeFieldVarBinary(Data[] data, FieldVector fieldVector, int from, int items) {
    VarBinaryVector varBinaryVector = (VarBinaryVector) fieldVector;
    varBinaryVector.setInitialCapacity(items);
    varBinaryVector.allocateNew();
    for (int i = 0; i < items; i++) {
      if (isSet() == 0) {
        varBinaryVector.setNull(i);
      } else {
        varBinaryVector.setIndexDefined(i);
        varBinaryVector.setValueLengthSafe(i, data[from + i].arr.length);
        varBinaryVector.setSafe(i, data[from + i].arr);
      }
    }
    varBinaryVector.setValueCount(items);
  }

  protected void writeFieldFloat4(Data[] data, FieldVector fieldVector, int from, int items) {
    Float4Vector float4Vector = (Float4Vector) fieldVector;
    float4Vector.setInitialCapacity(items);
    float4Vector.allocateNew();
    for (int i = 0; i < items; i++) {
      float4Vector.setSafe(i, isSet(), data[from + i].aFloat);
    }
    float4Vector.setValueCount(items);
  }

  protected void writeFieldFloat8(Data[] data, FieldVector fieldVector, int from, int items) {
    Float8Vector float8Vector = (Float8Vector) fieldVector;
    float8Vector.setInitialCapacity(items);
    float8Vector.allocateNew();
    for (int i = 0; i < items; i++) {
      float8Vector.setSafe(i, isSet(), data[from + i].aDouble);
    }
    float8Vector.setValueCount(items);
  }

  protected void writeFieldVarChar(Data[] data, FieldVector fieldVector, int from, int items) {
    VarCharVector valueVector = (VarCharVector) fieldVector;
    valueVector.setInitialCapacity(items);
    valueVector.allocateNew();
    for (int i = 0; i < items; i++) {
      valueVector.setSafe(i, new Text(data[from + i].aString));
    }
    valueVector.setValueCount(items);
  }

  private void showFieldLayout(Field field, FieldVector fieldVector) {
    TypeLayout typeLayout = TypeLayout.getTypeLayout(field.getType());
    List<BufferType> vectorTypes = typeLayout.getBufferTypes();
    ArrowBuf[] vectorBuffers = new ArrowBuf[vectorTypes.size()];

    if (vectorTypes.size() != vectorBuffers.length) {
      throw new IllegalArgumentException(
          "vector types and vector buffers are not the same size: " + vectorTypes.size() + " != "
              + vectorBuffers.length);
    }
    System.out.println("[" + field.toString() + "] : "+fieldVector.getClass().getCanonicalName());
    System.out.println(" \t TypeLayout is " + typeLayout.toString() + " vectorSize is " + vectorTypes.size());
    for (int i = 0; i < vectorTypes.size(); i++) {
      System.out.println(" \t vector type entries [" + i + "] " + vectorTypes.get(i).toString());
    }
    System.out.print("\n");
  }
}
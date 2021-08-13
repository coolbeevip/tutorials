package org.coolbeevip.arrow.labs;

import java.io.FileOutputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.BufferLayout.BufferType;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.TypeLayout;
import org.apache.arrow.vector.VarBinaryVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.util.Text;
import org.coolbeevip.arrow.schema.SchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanglei
 */
public class ArrowWriter<T> extends SchemaRepository {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private boolean useNullValues;
  private int batchSize;
  private VectorSchemaRoot root;
  private RootAllocator rootAllocator;
  private ArrowFileWriter arrowFileWriter;

  public ArrowWriter(Class<T> dataClass, FileOutputStream outputStream, int batchSize,
      boolean useNullValues,
      long allocatorLimit) {
    super(dataClass);
    this.batchSize = batchSize;
    this.useNullValues = useNullValues;
    // 定义内存分配
    this.rootAllocator = new RootAllocator(allocatorLimit);
    // 创建数据操作入口
    this.root = VectorSchemaRoot.create(getSchema(dataClass), this.rootAllocator);
    // 创建一个编码器
    DictionaryProvider.MapDictionaryProvider provider = new DictionaryProvider.MapDictionaryProvider();
    // 创建写文件对象
    this.arrowFileWriter = new ArrowFileWriter(root, provider, outputStream.getChannel());
    log.info(root.getSchema().toJson());
    showSchema();
  }

  private void showSchema() {
    log.debug("========== schema ==========");
    for (Field field : root.getSchema().getFields()) {
      FieldVector vector = root.getVector(field.getName());
      showFieldLayout(field, vector);
    }
    log.debug("========== schema ==========");
  }

  public void writeArrow(IData[] data) throws Exception {
    try {
      arrowFileWriter.start();
      int batchCount = 1;
      for (int rowIndex = 0; rowIndex < data.length; ) {
        // 从记录数和批次数中取最小值，作为单次写入的数量
        int toProcessItems = Math.min(this.batchSize, data.length - rowIndex);
        root.setRowCount(toProcessItems);
        log.info("写入第 {} 批", batchCount++);

        int columnIndex = 0;
        for (Field field : root.getSchema().getFields()) {
          FieldVector vector = root.getVector(field.getName());
          log.info("写入列 name={} columnIndex={} vectorType={} range[{}..{}]", vector.getName(),
              columnIndex, vector.getMinorType(), rowIndex, (rowIndex + toProcessItems - 1));
          switch (vector.getMinorType()) {
            case INT:
              writeFieldInt(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            case BIGINT:
              writeFieldLong(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            case VARBINARY:
              writeFieldVarBinary(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            case FLOAT4:
              writeFieldFloat4(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            case FLOAT8:
              writeFieldFloat8(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            case VARCHAR:
              writeFieldVarChar(data, vector, rowIndex, columnIndex, toProcessItems);
              break;
            default:
              throw new Exception(" Not supported yet type: " + vector.getMinorType());
          }
          columnIndex++;
        }
        arrowFileWriter.writeBatch();
        rowIndex += toProcessItems;
      }
    } finally {
      arrowFileWriter.end();
    }
  }

  protected void closeArrow() {
    arrowFileWriter.close();
  }

  private int isSet() {
//    if (useNullValues) {
//      return 0;
//    }
    return 1;
  }

  protected void writeFieldInt(IData[] data, FieldVector fieldVector, int rowIndex, int columnIndex,
      int items) {
    IntVector intVector = (IntVector) fieldVector;
    intVector.setInitialCapacity(items);
    intVector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      intVector.set(i, isSet(), (int) data[rowIndex + i].getValue(columnIndex));
    }
    fieldVector.setValueCount(items);
  }

  protected void writeFieldLong(IData[] data, FieldVector fieldVector, int rowIndex,
      int columnIndex, int items) {
    BigIntVector bigIntVector = (BigIntVector) fieldVector;
    bigIntVector.setInitialCapacity(items);
    bigIntVector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      bigIntVector.set(i, isSet(), (long) data[rowIndex + i].getValue(columnIndex));
    }
    bigIntVector.setValueCount(items);
  }

  protected void writeFieldVarBinary(IData[] data, FieldVector fieldVector, int rowIndex,
      int columnIndex,
      int items) {
    VarBinaryVector varBinaryVector = (VarBinaryVector) fieldVector;
    varBinaryVector.setInitialCapacity(items);
    varBinaryVector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      if (isSet() == 0) {
        varBinaryVector.setNull(i);
      } else {
        varBinaryVector.setIndexDefined(i);
        byte[] bytes = (byte[]) data[rowIndex + i].getValue(columnIndex);
        varBinaryVector.setValueLengthSafe(i, bytes.length);
        varBinaryVector.set(i, bytes);
      }
    }
    varBinaryVector.setValueCount(items);
  }

  protected void writeFieldFloat4(IData[] data, FieldVector fieldVector, int rowIndex,
      int columnIndex, int items) {
    Float4Vector float4Vector = (Float4Vector) fieldVector;
    float4Vector.setInitialCapacity(items);
    float4Vector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      float4Vector.set(i, isSet(), (float) data[rowIndex + i].getValue(columnIndex));
    }
    float4Vector.setValueCount(items);
  }

  protected void writeFieldFloat8(IData[] data, FieldVector fieldVector, int rowIndex,
      int columnIndex, int items) {
    Float8Vector float8Vector = (Float8Vector) fieldVector;
    float8Vector.setInitialCapacity(items);
    float8Vector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      float8Vector.set(i, isSet(), (double) data[rowIndex + i].getValue(columnIndex));
    }
    float8Vector.setValueCount(items);
  }

  protected void writeFieldVarChar(IData[] data, FieldVector fieldVector, int rowIndex,
      int columnIndex,
      int items) {
    VarCharVector valueVector = (VarCharVector) fieldVector;
    valueVector.setInitialCapacity(items);
    valueVector.allocateNew(items);
    for (int i = 0; i < items; i++) {
      valueVector.set(i, new Text(data[rowIndex + i].getValue(columnIndex).toString()));
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
    log.debug("[" + field.toString() + "] : " + fieldVector.getClass().getCanonicalName());
    log.debug(
        " \t TypeLayout is " + typeLayout.toString() + " vectorSize is " + vectorTypes.size());
    for (int i = 0; i < vectorTypes.size(); i++) {
      log.debug(" \t vector type entries [" + i + "] " + vectorTypes.get(i).toString());
    }
  }
}
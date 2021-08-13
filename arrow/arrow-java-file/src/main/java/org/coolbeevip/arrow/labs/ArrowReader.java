package org.coolbeevip.arrow.labs;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.ValueVector;
import org.apache.arrow.vector.VarBinaryVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.apache.arrow.vector.ipc.message.ArrowBlock;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanglei
 */
public class ArrowReader {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private VectorSchemaRoot root;
  private RootAllocator rootAllocator;
  private ArrowFileReader arrowFileReader;
  private long checkSumx;
  private long intCsum;
  private long longCsum;
  private long arrCsum;
  private long floatCsum;
  private long nullEntries;


  public ArrowReader(FileInputStream inputStream, long allocatorLimit) {
    // 定义内存分配
    this.rootAllocator = new RootAllocator(allocatorLimit);
    // 创建写文件对象
    this.arrowFileReader = new ArrowFileReader(new SeekableReadChannel(inputStream.getChannel()),
        this.rootAllocator);

  }

  public SampleData[] getData() throws Exception {
    SampleData[] data = new SampleData[0];
    // 创建数据操作入口
    this.root = arrowFileReader.getVectorSchemaRoot();
    // Schema
    log.debug(root.getSchema().toString());

    // 因为数据是分批写入，所以先读取块数据
    List<ArrowBlock> arrowBlocks = arrowFileReader.getRecordBlocks();
    log.info("Number of arrow blocks are " + arrowBlocks.size());

    // 读取每个块的数据
    for (int i = 0; i < arrowBlocks.size(); i++) {
      ArrowBlock rbBlock = arrowBlocks.get(i);
      if (!arrowFileReader.loadRecordBatch(rbBlock)) {
        throw new IOException("Expected to read record batch");
      }

      // 打印块信息
      log.info("\t[" + i + "] ArrowBlock, offset: " + rbBlock.getOffset() +
          ", metadataLength: " + rbBlock.getMetadataLength() +
          ", bodyLength " + rbBlock.getBodyLength());
      log.info("\t[" + i + "] row count for this block is " + root.getRowCount());

      List<FieldVector> fieldVector = root.getFieldVectors();
      log.info("\t[" + i + "] number of fieldVectors (corresponding to columns) : " + fieldVector
          .size());

      SampleData[] dataBlock = new SampleData[root.getRowCount()];

      for (int j = 0; j < fieldVector.size(); j++) {
        Types.MinorType mt = fieldVector.get(j).getMinorType();
        switch (mt) {
          case INT:
            showIntAccessor(dataBlock, fieldVector.get(j));
            break;
          case BIGINT:
            showBigIntAccessor(dataBlock, fieldVector.get(j));
            break;
          case VARBINARY:
            showVarBinaryAccessor(dataBlock, fieldVector.get(j));
            break;
          case FLOAT4:
            showFloat4Accessor(dataBlock, fieldVector.get(j));
            break;
          case FLOAT8:
            showFloat8Accessor(dataBlock, fieldVector.get(j));
            break;
          case VARCHAR:
            showVarCharAccessor(dataBlock, fieldVector.get(j));
            break;
          default:
            throw new Exception(" MinorType " + mt);
        }
      }
    }

    log.info("Done processing the file");
    arrowFileReader.close();
    long s1 = this.intCsum + this.longCsum + this.arrCsum + this.floatCsum;
    log.info(
        "intSum " + intCsum + " longSum " + longCsum + " arrSum " + arrCsum + " floatSum "
            + floatCsum + " = " + s1);
    System.err
        .println("Colsum Checksum > " + this.checkSumx + " , difference " + (s1 - this.checkSumx));
    return data;
  }

  private String getAccessorString(ValueVector accessor) {
    return "accessorType: " + accessor.getClass().getCanonicalName()
        + " valueCount " + accessor.getValueCount()
        + " nullCount " + accessor.getNullCount();
  }

  private void showAccessor(ValueVector accessor) {
    for (int j = 0; j < accessor.getValueCount(); j++) {
      if (!accessor.isNull(j)) {
        log.info("\t\t accessorType:  " + accessor.getClass().getCanonicalName()
            + " value[" + j + "] " + accessor.getObject(j));
      } else {
        this.nullEntries++;
        log.info(
            "\t\t accessorType:  " + accessor.getClass().getCanonicalName() + " NULL at " + j);
      }
    }
  }

  private void showIntAccessor(SampleData[] data, FieldVector fx) {
    IntVector intVector = ((IntVector) fx);
    for (int j = 0; j < intVector.getValueCount(); j++) {
      if (!intVector.isNull(j)) {
        int value = intVector.get(j);
        log.info("\t\t intAccessor[" + j + "] " + value);
        intCsum += value;
        this.checkSumx += value;
      } else {
        this.nullEntries++;
        log.info("\t\t intAccessor[" + j + "] : NULL ");
      }
    }
  }

  private void showBigIntAccessor(SampleData[] data, FieldVector fx) {
    BigIntVector bigIntVector = ((BigIntVector) fx);
    for (int j = 0; j < bigIntVector.getValueCount(); j++) {
      if (!bigIntVector.isNull(j)) {
        long value = bigIntVector.get(j);
        log.info("\t\t bigIntAccessor[" + j + "] " + value);
        longCsum += value;
        this.checkSumx += value;
      } else {
        this.nullEntries++;
        log.info("\t\t bigIntAccessor[" + j + "] : NULL ");
      }
    }
  }

  private void showVarBinaryAccessor(SampleData[] data, FieldVector fx) {
    VarBinaryVector varBinaryVector = ((VarBinaryVector) fx);
    for (int j = 0; j < varBinaryVector.getValueCount(); j++) {
      if (!varBinaryVector.isNull(j)) {
        byte[] value = varBinaryVector.get(j);
        long valHash = SampleData.hashArray(value);
        log.info("\t\t varBinaryAccessor[" + j + "] " + SampleData.firstX(value, 5));
        arrCsum += valHash;
        this.checkSumx += valHash;
      } else {
        this.nullEntries++;
        log.info("\t\t varBinaryAccessor[" + j + "] : NULL ");
      }
    }
  }

  private void showFloat4Accessor(SampleData[] data, FieldVector fx) {
    Float4Vector float4Vector = ((Float4Vector) fx);
    for (int j = 0; j < float4Vector.getValueCount(); j++) {
      if (!float4Vector.isNull(j)) {
        float value = float4Vector.get(j);
        log.info("\t\t float4[" + j + "] " + value);
        floatCsum += value;
        this.checkSumx += value;
      } else {
        this.nullEntries++;
        log.info("\t\t float4[" + j + "] : NULL ");
      }
    }
  }

  private void showFloat8Accessor(SampleData[] data, FieldVector fx) {
    Float8Vector float8Vector = ((Float8Vector) fx);
    for (int j = 0; j < float8Vector.getValueCount(); j++) {
      if (!float8Vector.isNull(j)) {
        double value = float8Vector.get(j);
        log.info("\t\t float8[" + j + "] " + value);
        floatCsum += value;
        this.checkSumx += value;
      } else {
        this.nullEntries++;
        log.info("\t\t float8[" + j + "] : NULL ");
      }
    }
  }

  private void showVarCharAccessor(SampleData[] data, FieldVector fx) {
    VarCharVector varCharVector = ((VarCharVector) fx);
    for (int j = 0; j < varCharVector.getValueCount(); j++) {
      if (!varCharVector.isNull(j)) {
        byte[] value = varCharVector.get(j);
        log.info("\t\t varchar[" + j + "] " + new Text(value));
        floatCsum += value.hashCode();
        this.checkSumx += value.hashCode();
      } else {
        this.nullEntries++;
        log.info("\t\t varchar[" + j + "] : NULL ");
      }
    }
  }
}
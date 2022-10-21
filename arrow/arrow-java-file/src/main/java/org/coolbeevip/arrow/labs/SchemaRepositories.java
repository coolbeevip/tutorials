package org.coolbeevip.arrow.labs;

import com.google.common.collect.ImmutableList;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.ArrowType.Binary;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.coolbeevip.arrow.annotation.ArrowField;
import org.coolbeevip.arrow.exception.FieldTypeNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

public class SchemaRepositories {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final Map<String, Schema> schemas = new ConcurrentHashMap<>();

  public void load(Class dataClass) {
    if (!schemas.containsKey(dataClass.getName())) {
      List<Field> fields = Arrays.stream(dataClass.getDeclaredFields())
          .filter(f -> f.getAnnotation(ArrowField.class) != null)
          .sorted((o1, o2) -> Integer.valueOf(o1.getAnnotation(ArrowField.class).index())
              .compareTo(Integer.valueOf(o2.getAnnotation(ArrowField.class).index())))
          .collect(Collectors.toList());

      ImmutableList.Builder<org.apache.arrow.vector.types.pojo.Field> schemaBuilder = ImmutableList
          .builder();

      fields.stream().forEach(field -> {
        log.info("index {} name {} type {}", field.getAnnotation(ArrowField.class).index(),
            field.getName(), field.getType().getName());

        if (field.getType().equals(int.class) || field.getType().equals(Integer.TYPE)) {
          // 32位有符号整数
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(), FieldType
              .nullable(new ArrowType.Int(32, true)), null));
        } else if (field.getType().equals(long.class) || field.getType().equals(Long.TYPE)) {
          // 42位有符号整数
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(new ArrowType.Int(64, true)), null));
        } else if (field.getType().equals(float.class) || field.getType().equals(Float.TYPE)) {
          // 有符号单精度浮点
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(new ArrowType.FloatingPoint(SINGLE)), null));
        } else if (field.getType().equals(double.class) || field.getType().equals(Double.TYPE)) {
          // 有符号双精度浮点
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(new ArrowType.FloatingPoint(DOUBLE)), null));
        } else if (field.getType().equals(String.class)) {
          // 字符串
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(ArrowType.Utf8.INSTANCE), null));
        } else if (field.getType().equals(byte[].class)) {
          // 字节数组
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(Binary.INSTANCE), null));
        } else if (schemas.containsKey(field.getType().getName())) {
          schemaBuilder.add(new org.apache.arrow.vector.types.pojo.Field(field.getName(),
              FieldType.nullable(ArrowType.Struct.INSTANCE), schemas.get(field.getType().getName()).getFields()));
        } else {
          throw new FieldTypeNotSupportedException(
              "field " + field.getName() + " type " + field.getType() + " Not Supported");
        }

      });

      schemas.put(dataClass.getName(), new Schema(schemaBuilder.build(), null));
    }
  }

  public Schema getSchema(Class dataClass) {
    return schemas.get(dataClass.getName());
  }
}
package com.coolbeevip.rocksdb.serialization;

import com.coolbeevip.rocksdb.Serialization;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author zhanglei
 */
public class KryoSerialization implements Serialization {

  Kryo kryo = new Kryo();

  public KryoSerialization(Class... classes) {
    Arrays.stream(classes).forEach(clz -> kryo.register(clz));
  }

  @Override
  public <T> byte[] serialize(T object) {
    ByteArrayOutputStream bos = null;
    Output output = null;
    try {
      bos = new ByteArrayOutputStream();
      output = new Output(bos);
      kryo.writeObject(output, object);
    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      output.close();
    }
    return bos.toByteArray();
  }

  @Override
  public <T> T deserialize(Class<T> clazz, byte[] bytes) {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    Input input = new Input(bis);
    try {
      return kryo.readObject(input, clazz);
    } finally {
      try {
        bis.close();
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
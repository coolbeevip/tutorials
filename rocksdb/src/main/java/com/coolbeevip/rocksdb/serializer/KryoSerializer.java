package com.coolbeevip.rocksdb.serializer;

import com.coolbeevip.rocksdb.core.RocksDbSerializer;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zhanglei
 */
public class KryoSerializer<T> implements RocksDbSerializer<T> {

  final Class<T> clz;
  Kryo kryo = new Kryo();

  public KryoSerializer(Class<T> clz) {
    this.clz = clz;
    kryo.register(clz);
  }

  @Override
  public T deserialize(byte[] data) {
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    Input input = new Input(bis);
    try {
      return kryo.readObject(input, this.clz);
    } finally {
      try {
        bis.close();
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public byte[] serialize(T value) {
    ByteArrayOutputStream bos = null;
    Output output = null;
    try {
      bos = new ByteArrayOutputStream();
      output = new Output(bos);
      kryo.writeObject(output, value);
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

//  @Override
//  public Class<T> getClz() {
//    return this.clz;
//  }
}
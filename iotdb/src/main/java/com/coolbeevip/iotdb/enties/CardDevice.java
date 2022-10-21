package com.coolbeevip.iotdb.enties;

import com.coolbeevip.iotdb.core.DeviceEntity;
import org.apache.iotdb.service.rpc.thrift.TSCreateTimeseriesReq;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CardDevice extends DeviceEntity {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends DeviceEntity {

    private String storageGroup;

    public Builder session(Session session) {
      this.session = session;
      return this;
    }

    public Builder storageGroup(String storageGroup) {
      this.storageGroup = storageGroup;
      return this;
    }

    public Builder paths(Set<String> paths) {
      this.paths = paths;
      return this;
    }

    public Builder addPath(String path) {
      this.paths.add(path);
      return this;
    }

    public Builder addMeasurement(String measurementId, TSDataType type, TSEncoding encoding,
                                  CompressionType compressionType, Map<String, String> props, Map<String, String> tags,
                                  Map<String, String> attributes, String measurementAlias) {
      TSCreateTimeseriesReq request = new TSCreateTimeseriesReq();
      request.setPath(
          this.storageGroup + "." + this.paths.stream().collect(Collectors.joining(".")) + "."
              + measurementId);
      request.setDataType(type.ordinal());
      request.setEncoding(encoding.ordinal());
      request.setCompressor(compressionType.ordinal());
      request.setProps(props);
      request.setTags(tags);
      request.setAttributes(attributes);
      request.setMeasurementAlias(measurementAlias);
      this.measurements.add(request);
      return this;
    }

    public CardDevice build() {
      CardDevice cardEntity = new CardDevice();
      cardEntity.setSession(this.session);
      cardEntity.setStorageGroup(this.storageGroup);
      cardEntity.setPaths(this.paths);
      cardEntity.setMeasurements(this.measurements);
      return cardEntity;
    }
  }
}
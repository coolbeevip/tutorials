
Build

```shell
./mvnw clean package -DskipTests
```

Download [opentelemetry-javaagent.jar](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar)

```shell
curl -O https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

启动被采集设备

```shell
java -javaagent:/Users/zhanglei/Downloads/opentelemetry-javaagent.jar \
-Djava.net.preferIPv4Stack=true \
-Dotel.traces.exporter=logging \
-Dotel.metrics.exporter=logging \
-Dotel.logs.exporter=logging \
-jar open-telemetry/telemetry-device/target/telemetry-device-1.0.0-SNAPSHOT-exec.jar 
```

启动采集器

```shell
java -javaagent:/Users/zhanglei/Downloads/opentelemetry-javaagent.jar \
-Djava.net.preferIPv4Stack=true \
-Dotel.traces.exporter=logging \
-Dotel.metrics.exporter=logging \
-Dotel.logs.exporter=logging \
-jar open-telemetry/telemetry-collector/target/telemetry-collector-1.0.0-SNAPSHOT-exec.jar
```
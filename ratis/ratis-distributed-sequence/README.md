
## Build & Test

```
mvn clean package
```

## Benchmark

Build

```
./mvnw clean package -T 1.5C -Pjmh -DskipTests
```

Run

```
java -jar ratis/ratis-distributed-sequence/target/benchmark-jmh.jar
```


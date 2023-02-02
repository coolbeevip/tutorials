## 运行前准备

本样例需要先开启 9998 端口监听模式，然后等待 JOB 启动后连接，并在连接后在此终端发送单词

```shell
nc -lk 9998
```

## 本地环境运行

编译本地运行环境 JAR，将生成 `target/local-job-word-count-1.0.0-SNAPSHOT.jar` 部署包

```shell
./mvnw clean package -pl flink/word-count -Plocal
```

运行

```shell
java -jar flink/word-count/target/local-job-word-count-1.0.0-SNAPSHOT.jar --env LOCAL --host localhost --port 9998
```

## 提交到集群运行

编译 Flink 集群运行环境 JAR，将生成 `target/flink-job-word-count-1.0.0-SNAPSHOT.jar` 部署包

```shell
./mvnw clean package -pl flink/word-count
```

使用 Docker 启动 flink 集群，在浏览器中访问 http://localhost:8081/ 地址，打开 flink 管理界面

```shell
cd flink/docker
docker-compose -f docker-compose-session-mode.yml up -d
```

启动 job 提交命令行并挂在要提交的 jar 路径

```shell
docker run -it --rm -v /Users/zhanglei/Work/github/tutorials/flink/word-count/target:/jobs flink:1.14.6-scala_2.12 /bin/bash
```

提交 job 

```shell
flink run -m 10.1.75.126:8081 /jobs/flink-job-word-count-1.0.0-SNAPSHOT.jar --host 10.1.75.126 --port 9998
```

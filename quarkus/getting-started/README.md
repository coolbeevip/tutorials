# Quarkus Started

## 前提

* JDK 1.8+ 

* Apache Maven 3.5.3+

* GraalVM （编译本地可执行程序时需要）

  https://github.com/oracle/graal/releases/download/vm-19.2.0/graalvm-ce-darwin-amd64-19.2.0.tar.gz

  https://github.com/oracle/graal/releases/download/vm-19.2.0/native-image-installable-svm-darwin-amd64-19.2.0.jar

## 快速开始

编译

```bash
./mvnw test package
```

运行

```bash
java -jar target/getting-started-1.0-SNAPSHOT-runner.jar
```

测试接口

```bash
$ curl http://localhost:8080/hello
hello
```

调试

1. 热部署运行

```bash
./mvnw compile quarkus:dev
```

2. 远程调试模式运行

```bash
./mvnw compile quarkus:dev -Ddebug=true
```

开启 IDE 远程调试（默认端口 5005）

## 编译成本地可执行程序

编译成本地可执行程序有三种办法

1. 安装 GraalVM 并在本机编译成可执行程序后运行
2. 安装 GraalVM 和 Docker 直接在本机制作 Docker镜像后运行
3. 不安装 GraalVM，使用多阶段Docker镜像制作方法制作Docker镜像后运行

#### GraalVM

> 需要本机安装 GraalVM 和 Native-image Tool 
>

编译

```bash
./mvnw package -Pnative
```

运行

```bash
./target/getting-started-1.0-SNAPSHOT-runner
```

测试

```bash
./mvnw verify -Pnative
```

#### GraalVM + Docker

> 需要 Docker 环境
>
> 需要本机安装 GraalVM 和 Native-image Tool 



使用Docker镜像编译出Linux版本下的可执行文件，(从 quay.io 拉取镜像会非常慢，建议直接使用下边的 Multi-Stage Docker 方法 )

```bash
./mvnw package -Pnative -Dnative-image.docker-build=true
```

生成镜像

```bash
docker build -f src/main/docker/Dockerfile.native -t quarkus-quickstart/getting-started .
```

启动

```bash
docker run -i --rm -p 8080:8080 quarkus-quickstart/getting-started
```

#### Multi-Stage Docker

> 需要 Docker 环境，注意如果根目录下有隐藏文件 .dockerignore, 请先删除这个文件

Docker 会从 quay.io 拉取 quay.io/quarkus/centos-quarkus-maven 镜像，因为 quay.io 速度较慢，你也可以从 DockerHub 上拉取后重新命名后使用

```bash
docker pull coolbeevip/centos-quarkus-maven:19.1.1
docker pull coolbeevip/ubi8-ubi-minimal
docker tag coolbeevip/centos-quarkus-maven:19.1.1 quay.io/quarkus/centos-quarkus-maven:19.1.1
docker tag coolbeevip/ubi8-ubi-minimal registry.access.redhat.com/ubi8/ubi-minimal
```

制作镜像

```bash
docker build -f src/main/docker/Dockerfile.multistage -t quarkus-quickstart/getting-started .
```

启动

```bash
docker run -i --rm -p 8080:8080 quarkus-quickstart/getting-started
```


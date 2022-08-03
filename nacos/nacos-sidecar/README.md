# Nacos SideCar

## 启动 Nacos Server

X86/AMD64

```yaml
docker run -d --name --rm nacos-quick -e MODE=standalone -p 8849:8848 nacos/nacos-server:2.0.3-slim
```

macOS M1

```shell
docker run -d --rm --name nacos-quick -e MODE=standalone -p 8848:8848 -p 9848:9848 zill057/nacos-server-apple-silicon:2.0.3
```

NACOS_AUTH_ENABLE
## Register

```shell
java -DSERVER_ADDR=127.0.0.1:8848 \
    -DNAMESPACE="public" \
    -DGROUP_NAME="DEFAULT_GROUP" \
    -DSERVICE_NAME="myservice" \
    -DSERVICE_IP="10.19.88.60" \
    -DSERVICE_PORT=6000 \
    -DUSERNAME="nacos" \
    -DPASSWORD="nacos" \
    -jar target/nacos-sidecar-1.0.0-SNAPSHOT.jar
```
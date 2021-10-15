## 生成 keystore.jks 

```shell
keytool \
-genkeypair \
-keystore keystore.jks \
-alias coolbeeviptutorials \
-keypass 123456 \
-storepass 123456 \
-keyalg RSA \
-keysize 2048 \
-validity 365000 \
-dname "CN=tutorials.coolbeevip.com,OU=Test,O=Test,L=Beijing,S=Beijing,C=CN"
```

查看生成的文件信息

```shell
keytool -list -v -storepass "123456" -keystore keystore.jks 
```

## 生成 truststore.jks

从 keystore 中导自签署证书

```shell
keytool -export -alias coolbeeviptutorials -keystore keystore.jks -storepass "123456" -rfc -file selfsignedcert.cer
```

将自签署证书导出到 truststore.jks

```
keytool -import -alias coolbeeviptutorials -file selfsignedcert.cer -storepass "123456" -noprompt -keystore truststore.jks
```

查看 truststore.jks

```
keytool -list -v -storepass "123456" -keystore truststore.jks
```
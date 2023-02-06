# 通过 maven-shade-plugin 解决依赖冲突

## 演示冲突

> myproject 项目存在依赖冲突，而我们有必须同时拥有 Joda 2.1 和 2.8 两个版本

* elasticsearch 2.0.0 版本依赖 Joda 2.8
* 你的项目依赖 Joda 2.1

## 解决办法

#### 增加 elasticsearch-shade 模块

创建一个 elasticsearch 2.0.0 的 shade 版本，这个版本包含了使用 joda 的所有代码，并且将 org.joda 重命名为 my.elasticsearch.joda，打完包后你可以看到一个
elasticsearch-shade-1.0.0-SNAPSHOT.jar 文件，此文件中可以看到 my.elasticsearch.joda 并且相关引用 joda 的类也改为了引用 my.elasticsearch.joda 

#### 使用 elasticsearch-shade 模块

在 myproject 项目中用 elasticsearch-shade 依赖替换 elasticsearch 依赖

#### 验证

Main 方法中同时包含了 org.joda.time.DateTime 和 my.elasticsearch.joda.time.DateTime 的引用，此时两个版本 Joda 共存

```shell
$ java -jar myproject-1.0.0-SNAPSHOT.jar
unshaded = (file:/Users/zhanglei/Work/github/tutorials/maven/maven-shade-plugin-solve-deps-conflict/myproject/target/lib/joda-time-2.1.jar <no signer certificates>)
shaded = (file:/Users/zhanglei/Work/github/tutorials/maven/maven-shade-plugin-solve-deps-conflict/myproject/target/lib/elasticsearch-shade-1.0.0-SNAPSHOT.jar <no signer certificates>)
```
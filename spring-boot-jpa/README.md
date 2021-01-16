# JPA example

* 验证 JPA 常用方法
* 验证数据库兼容性

## 集成测试


### H2 Database
```
mvn verify -P h2
```

### MySQL

```
mvn verify -P mysql
```

### Postgresql

```
mvn verify -P postgresql
```

### Oracle 11g

```
mvn verify -P oracle
```

# ISSUE

oracle

* 使用 ddl-auto=create_drop 时，在 session 关闭时执行删除表失败

```
org.hibernate.tool.schema.spi.CommandAcceptanceException: Error executing DDL "drop table CUSTOMER cascade constraints" via JDBC Statement
```


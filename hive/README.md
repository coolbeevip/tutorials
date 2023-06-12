## Docker Composer for Hive

创建外部数据存储目录 `./volume/hive`，并使用 `chmod 777 ./volume/hive` 修改权限。

```yaml
version: '3.0'
services:
  postgres:
    image: postgres
    restart: unless-stopped
    container_name: postgres
    hostname: postgres
    privileged: true
    environment:
      POSTGRES_DB: 'metastore_db'
      POSTGRES_USER: 'hive'
      POSTGRES_PASSWORD: 'password'
    ports:
      - '5432:5432'
    volumes:
      - ./volume/hive/postgresql:/var/lib/postgresql

  metastore:
    image: apache/hive:4.0.0-alpha-2
    depends_on:
      - postgres
    restart: unless-stopped
    container_name: metastore
    hostname: metastore
    privileged: true
    environment:
      DB_DRIVER: postgres
      SERVICE_NAME: 'metastore'
      SERVICE_OPTS: '-Xmx1G -Djavax.jdo.option.ConnectionDriverName=org.postgresql.Driver
                     -Djavax.jdo.option.ConnectionURL=jdbc:postgresql://postgres:5432/metastore_db
                     -Djavax.jdo.option.ConnectionUserName=hive
                     -Djavax.jdo.option.ConnectionPassword=password'
    ports:
      - '9083:9083'
    volumes:
      - ./volume/hive/data/warehouse:/opt/hive/data/warehouse

  hiveserver2:
    image: apache/hive:4.0.0-alpha-2
    depends_on:
      - metastore
    restart: unless-stopped
    container_name: hiveserver2
    privileged: true
    environment:
      HIVE_SERVER2_THRIFT_PORT: 10000
      SERVICE_OPTS: '-Xmx1G -Dhive.metastore.uris=thrift://metastore:9083'
      IS_RESUME: 'true'
      SERVICE_NAME: 'hiveserver2'
    ports:
      - '10000:10000'
      - '10002:10002'
    volumes:
      - ./volume/hive/data/warehouse:/opt/hive/data/warehouse
```



* HiveServer2 web Accessed on browser at http://localhost:10002/
* Beeline `docker exec -it hiveserver2 beeline -u 'jdbc:hive2://hiveserver2:10000/'`
* Run some queries
  ```sql
  show tables;
  create table hive_example(a string, b int) partitioned by(c int);
  alter table hive_example add partition(c=1);
  insert into hive_example partition(c=1) values('a', 1), ('a', 2),('b',3);
  select count(distinct a) from hive_example;
  select sum(b) from hive_example;
  ```

load data local inpath '/opt/hive/data/warehouse/import/data.csv' into table hive_example;
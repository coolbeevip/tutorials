
# Schema

* 存储组: `root.test`
* 时间序列: `<省>.<市>.<站>.<厂家>.<网元ID>.CARD.<板卡ID>.<周期>.<性能指标名称>`
* 周期: 枚举值；24,15

## 数据样例

* 板卡温度指标

root.test.湖北.武汉.NULL.华为.TRANS_ELEMENT-ff8081816c21dc05016c22bdca284f65.CARD.CARD-8a5b8486656aa31001656b6d35d6768e.15.temperature

## 数据规模

一个中型省份包含大概 9.5 万网元（50,000PTN网元；20,000SDH网元；5,000OTN网元；20,000SPN网元）

模拟地市数量：12个
模拟站点：固定NULL
模拟厂家：随机华为，中兴，西门子，诺基亚
模拟每个地市网元数：8000
模拟每个网元板卡数；3个

## 基准测试

* 10万无并发

```shell
 IoTDB Batch Insert
----------------- params ------------------
device: root.test.省份.城市.机房.厂家.TRANS_ELEMENT-f986cb7ac0a84ac490acb348c17c20a1.CARD.CARD-039b2488ee63409c9be9f48c05f74b2a
requests: 100000
threads: 1
----------------- output ------------------
total time: 00 min, 02 sec
throughput request: 50000.0 ops/s
throughput request size: 0 KB ops/s
throughput response size: 0 KB ops/s
==========================================
```

* 10万10并发

```
==========================================
IoTDB Batch Insert
----------------- params ------------------
device: root.test.省份.城市.机房.厂家.ELEMENT-5be70459cc7c4b90936620043525eac9.CARD.CARD-a79fd303b4104744b51a224d6b565ef7
requests: 100000
threads: 10
----------------- output ------------------
total time: 00 min, 28 sec
throughput request: 3571.4285 ops/s
throughput request size: 0 KB ops/s
throughput response size: 0 KB ops/s
==========================================
```

## 查询

```sql
select temperature from root.test.湖北.武汉.NIL.华为.TRANS_ELEMENT-ff8081816c21dc05016c22bdca284f65.CARD.CARD-8a5b8486656aa31001656b6d35d6768e
```



## CLI

start-cli.sh -h 10.19.32.51 -p 6667 -u root -pw root


创建存储组

```sql
set storage group to root.test
```

显示存储组

```sql
show storage group
```

显示时间序列

```sql
show timeseries
```

删除时间序列

```sql
delete timeseries xxx
```
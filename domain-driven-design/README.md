# 领域模型设计包组织

## 包结构

### Interface 接口层（Adapter 层）

* 与其他系统交互的接口与通信设置（例如：Web Service、REST、RPC）
* 主要由 Facade，DTO，Assembler 三类组件组成
* DTO 数据传输对象，目标是：减少网络流量、简化远程接口和对象、数据组合
* Assembler 数据装配：用于 DTO 对象和领域对象的转换
* Facade 用于为调用者提供粗力度的接口，目标：用于将一个请求委派给多个服务、减少各层之间的耦合、提高性能、集中安全管理、向客户端提供更少的远程接口
* Facade 是也个非必须组件，可以合并到 Application 层的 Service Facade
* DTO 转换为 CQRS 命令对象

### Application 应用层（User Case 层）

* 主要提供各种 Service
* 只负责协调并委派业务逻辑给领域对象不处理业务逻辑
* 交互层：应用层其他 Service、Domain、Infrastructure
* 接口和实现分离
* 负责协调领域层与基础设施层

### Domain 领域层

* 全部核心业务逻辑
* 包含 Entity（实体）、ValueObject(值对象)、Domain Event（领域事件）、Repository（仓储）
* 与任何具体框架和平台技术无关，实现核心业务与外部依赖的隔离

### SPI 

* 使用 SPI 基础设施服务发现
* 通过领域事件驱动基础设施

### Infrastructure 共享基础设置（Adapter 层）

* 为 Interface、Application、Domain 层提供支撑
* 所有与具体平台、框架相关的实现都在此层提供

以上五层调用关系 Interface -> Application -> Domain -> SPI -> Infrastructure

+-----------+     +-------------+    +--------+   +-------+  +---------------+
| interface +-----> application +----> domain +--->  spi  +--> infrastructure|
+-----------+     +-------------+    +--------+   +-------+  +---------------+

## CQRS

* 查询可直接定义查询模型，不一定使用领域模型
* Command 命令写入：改变状态（领域模型）
* Query 查询读取：不改变状态（领域模型 或 查询模型）
* 使用事件风暴建模，梳理领域事件和命令
* 命令与查询同步（分布式事务写入业务库与查询库 或 后台同步）
* CQRS 必然引入最终一致性问题，因为查询和命令模型是独立的组件

CQRS 与 DDD 层

Interface -> Application -[Command & Query] -> Domain -> SPI -> Infrastructure

命令对象的约束

* 命令对象属于值对象，所有字段都应是 final

## 学习路线图

* Domain Primitive 基础数据类型
* 持续发现和交付：Event Storming > Context Map > Design Heuristics > Modelling
* 降低架构腐败速度：通过 Anti-Corruption Layer 集成第三方库的模块化方案
* 标准组件的规范和边界：Entity, Aggregate, Repository, Domain Service, Application Service, Event, DTO Assembler 等
* 基于 Use Case 重定义应用服务的边界
* 基于 DDD 的微服务化改造及颗粒度控制
* CQRS 架构的改造和挑战
* 基于事件驱动的架构的挑战
* 需要一个 maven 插件，实现一些包设计上的依赖检查

## 关键字

* [CQRS](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation#Command_query_responsibility_segregation)
* [Hexagonal architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
* [Event Sourcing](https://en.wikipedia.org/wiki/Domain-driven_design)

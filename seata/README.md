# Seata 分布式事务示例

本项目演示了使用 Seata 实现分布式事务的三种模式：AT、TCC 和 SAGA。

## 项目结构

```
├── src/main/java/org/example/seata
│   ├── common                 # 公共工具类
│   │   ├── BusinessException.java    # 业务异常类
│   │   ├── DatabaseConnection.java   # 数据库连接工具类
│   │   └── SeataInit.java            # Seata 客户端初始化工具类
│   ├── entity                 # 实体类
│   │   ├── Account.java              # 账户实体类
│   │   ├── Order.java                # 订单实体类
│   │   └── Storage.java              # 库存实体类
│   ├── service                # AT 模式服务接口和实现
│   │   ├── AccountService.java       # 账户服务接口
│   │   ├── BusinessService.java      # 业务服务接口
│   │   ├── OrderService.java         # 订单服务接口
│   │   ├── StorageService.java       # 库存服务接口
│   │   └── impl                      # 服务实现类
│   ├── tcc                    # TCC 模式服务接口和实现
│   │   ├── AccountTCCService.java    # 账户 TCC 服务接口
│   │   ├── OrderTCCService.java      # 订单 TCC 服务接口
│   │   ├── StorageTCCService.java    # 库存 TCC 服务接口
│   │   └── impl                      # TCC 服务实现类
│   ├── saga                   # SAGA 模式服务接口和实现
│   │   ├── AccountSagaService.java   # 账户 SAGA 服务接口
│   │   ├── OrderSagaService.java     # 订单 SAGA 服务接口
│   │   ├── StorageSagaService.java   # 库存 SAGA 服务接口
│   │   └── impl                      # SAGA 服务实现类
│   └── Main.java              # 主类，用于测试三种模式
└── src/main/resources
    ├── db_init.sql            # 数据库初始化脚本
    ├── file.conf              # Seata 配置文件
    ├── logback.xml            # 日志配置文件
    └── registry.conf          # Seata 注册中心配置文件
```

## 环境准备

### 1. 安装 MySQL

确保已安装 MySQL 数据库（推荐 5.7 或 8.0 版本）。

### 2. 初始化数据库

执行 `src/main/resources/db_init.sql` 脚本，创建所需的数据库和表：

```bash
mysql -u root -p < src/main/resources/db_init.sql
```

### 3. 下载并启动 Seata Server

1. 从 [Seata 官方网站](https://seata.io/zh-cn/blog/download.html) 下载与项目依赖版本相同的 Seata Server（本项目使用 1.7.0 版本）
2. 解压下载的文件
3. 将项目中的 `file.conf` 和 `registry.conf` 复制到 Seata Server 的 `conf` 目录下
4. 启动 Seata Server：

```bash
# Windows
bin\seata-server.bat

# Linux/Mac
sh bin/seata-server.sh
```

## 运行示例

1. 确保 MySQL 和 Seata Server 已启动
2. 运行 `Main` 类：

```bash
mvn exec:java -Dexec.mainClass="org.example.seata.Main"
```

或者直接在 IDE 中运行 `Main` 类。

## 分布式事务模式说明

### AT 模式（自动事务模式）

- 优点：对业务代码侵入性小，使用简单
- 缺点：依赖数据库的事务能力，需要数据库支持
- 适用场景：简单的 CRUD 操作，对性能要求不高的场景

### TCC 模式（Try-Confirm-Cancel）

- 优点：性能较好，不依赖数据库事务能力
- 缺点：代码侵入性大，需要实现三个接口
- 适用场景：对性能要求较高，或者需要支持非事务性资源的场景

### SAGA 模式

- 优点：长事务支持，事务补偿机制
- 缺点：一致性较弱，只能保证最终一致性
- 适用场景：长事务，业务流程复杂的场景

## 测试场景

本示例模拟了一个简单的下单流程：

1. 创建订单（订单服务）
2. 扣减库存（库存服务）
3. 扣减账户余额（账户服务）
4. 更新订单状态（订单服务）

可以通过修改购买数量来测试不同的场景：

- 正常场景：购买数量小于库存且总金额小于账户余额
- 库存不足：购买数量大于库存
- 余额不足：总金额大于账户余额

## 注意事项

1. 本示例使用的是 File 模式的配置中心和注册中心，生产环境建议使用 Nacos、ZooKeeper 等
2. 本示例没有使用 Spring，而是手动管理事务和资源，实际项目中可以结合 Spring 使用
3. 本示例使用的是本地数据库，实际项目中各个服务可能部署在不同的机器上
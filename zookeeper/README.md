# ZooKeeper 常见用法示例

本项目展示了 ZooKeeper 在分布式系统中的常见应用场景和实现方式，包括服务注册与发现、分布式锁、配置管理和 Leader 选举等功能。

## 项目结构

```
src/main/java/com/example/zookeeper/
├── config/                 # 配置中心实现
│   └── ConfigCenter.java   # 基于ZooKeeper的配置管理
├── election/               # Leader选举实现
│   └── LeaderElection.java # 基于Curator的Leader选举
├── example/                # 示例应用
│   └── ZKExample.java      # 各功能的使用示例
├── lock/                   # 分布式锁实现
│   ├── DistributedLock.java # 基于Curator的分布式锁（互斥锁）
│   └── SharedLock.java     # 基于Curator的共享锁（读写锁）
├── registry/               # 服务注册与发现
│   ├── ServiceDiscovery.java # 服务发现实现
│   └── ServiceRegistry.java  # 服务注册实现
└── util/                   # 工具类
    └── ZKClientUtil.java   # ZooKeeper客户端工具
```

## 功能说明

### 1. 服务注册与发现

- **ServiceRegistry**: 提供服务注册功能，将服务信息注册到ZooKeeper中
- **ServiceDiscovery**: 提供服务发现功能，从ZooKeeper中发现可用的服务

### 2. 分布式锁

- **DistributedLock**: 基于Curator的InterProcessMutex实现分布式锁，提供互斥访问控制
- **SharedLock**: 基于Curator的InterProcessReadWriteLock实现共享锁，提供读写分离的访问控制，允许多个客户端同时读取，但写入时需要独占

### 3. 配置管理

- **ConfigCenter**: 基于ZooKeeper实现的配置中心，支持动态更新配置和配置变更通知

### 4. Leader选举

- **LeaderElection**: 基于Curator的LeaderSelector实现Leader选举，用于主备切换和任务分配

## 使用方法

### 前提条件

1. 安装并启动ZooKeeper服务器
2. 修改`ZKClientUtil.java`中的`ZK_CONNECTION_STRING`为你的ZooKeeper服务器地址

### 运行示例

直接运行`ZKExample.java`的main方法，将展示所有功能的使用示例。

```java
public static void main(String[] args) {
    try {
        // 初始化ZooKeeper客户端
        ZKClientUtil.getClient();
        
        // 运行各种示例
        registryExample();    // 服务注册与发现示例
        lockExample();        // 分布式锁示例
        configExample();      // 配置中心示例
        leaderElectionExample(); // Leader选举示例
        
        // 关闭ZooKeeper客户端
        ZKClientUtil.closeClient();
    } catch (Exception e) {
        logger.error("示例运行出错", e);
    }
}
```

## 注意事项

1. 本示例代码需要连接到实际的ZooKeeper服务器才能运行
2. 在生产环境中使用时，需要考虑更多的异常处理和重试机制
3. ZooKeeper适合存储小量的配置数据，不适合存储大量数据
4. 分布式锁和Leader选举在网络分区情况下可能会出现问题，需要谨慎使用

## 扩展阅读

- [ZooKeeper官方文档](https://zookeeper.apache.org/doc/current/)
- [Curator框架文档](https://curator.apache.org/)
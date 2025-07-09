#!/bin/bash

echo "===== Seata 分布式事务示例运行脚本 ====="

SEATA_VERSION=1.7.0
DOWNLOAD_DIR="$(pwd)/download"
SEATA_DIR="${DOWNLOAD_DIR}/seata-${SEATA_VERSION}"

# 创建下载目录
if [ ! -d "${DOWNLOAD_DIR}" ]; then
    echo "创建下载目录..."
    mkdir -p "${DOWNLOAD_DIR}"
fi

# 检查 Seata Server 是否已下载
if [ ! -d "${SEATA_DIR}" ]; then
    echo "Seata Server 未下载，正在下载..."
    echo "请从 https://github.com/seata/seata/releases/download/v${SEATA_VERSION}/seata-server-${SEATA_VERSION}.tar.gz 手动下载"
    echo "并解压到 ${DOWNLOAD_DIR} 目录"
    echo "然后重新运行此脚本"
    read -p "按任意键继续..."
    exit 1
fi

# 复制配置文件到 Seata Server
echo "复制配置文件到 Seata Server..."
cp -f "$(pwd)/src/main/resources/file.conf" "${SEATA_DIR}/conf/"
cp -f "$(pwd)/src/main/resources/registry.conf" "${SEATA_DIR}/conf/"

# 提示用户初始化数据库
echo "请确保已经初始化数据库，如果尚未初始化，请执行以下步骤："
echo "1. 打开 MySQL 客户端"
echo "2. 执行 $(pwd)/src/main/resources/db_init.sql 脚本"
echo ""
read -p "是否已初始化数据库？(Y/N) " DB_INIT

if [ "${DB_INIT}" = "N" ] || [ "${DB_INIT}" = "n" ]; then
    echo "请先初始化数据库，然后重新运行此脚本"
    read -p "按任意键继续..."
    exit 1
fi

# 启动 Seata Server
echo "正在启动 Seata Server..."
cd "${SEATA_DIR}/bin"
./seata-server.sh -p 8091 &
SEATA_PID=$!

# 等待 Seata Server 启动
echo "等待 Seata Server 启动..."
sleep 10

# 运行测试
echo "正在编译项目..."
cd "$(pwd)"
mvn clean compile

echo "运行交互式测试..."
echo "1. 运行 Main 类（交互式测试）"
echo "2. 运行 TransactionTest 类（自动化测试）"
read -p "请选择要运行的测试（1/2）：" TEST_CHOICE

if [ "${TEST_CHOICE}" = "1" ]; then
    echo "运行 Main 类..."
    mvn exec:java -Dexec.mainClass="org.example.seata.Main"
elif [ "${TEST_CHOICE}" = "2" ]; then
    echo "运行 TransactionTest 类..."
    mvn exec:java -Dexec.mainClass="org.example.seata.TransactionTest"
else
    echo "无效的选择"
fi

echo "测试完成"
read -p "是否关闭 Seata Server？(Y/N) " CLOSE_SERVER

if [ "${CLOSE_SERVER}" = "Y" ] || [ "${CLOSE_SERVER}" = "y" ]; then
    echo "正在关闭 Seata Server..."
    kill ${SEATA_PID}
    echo "Seata Server 已关闭"
fi

read -p "按任意键继续..."
@echo off
echo ===== Seata 分布式事务示例运行脚本 =====

set SEATA_VERSION=1.7.0
set DOWNLOAD_DIR=%~dp0download
set SEATA_DIR=%DOWNLOAD_DIR%\seata-%SEATA_VERSION%

REM 创建下载目录
if not exist %DOWNLOAD_DIR% (
    echo 创建下载目录...
    mkdir %DOWNLOAD_DIR%
)

REM 检查 Seata Server 是否已下载
if not exist %SEATA_DIR% (
    echo Seata Server 未下载，正在下载...
    echo 请从 https://github.com/seata/seata/releases/download/v%SEATA_VERSION%/seata-server-%SEATA_VERSION%.zip 手动下载
    echo 并解压到 %DOWNLOAD_DIR% 目录
    echo 然后重新运行此脚本
    pause
    exit /b
)

REM 复制配置文件到 Seata Server
echo 复制配置文件到 Seata Server...
copy /Y "%~dp0src\main\resources\file.conf" "%SEATA_DIR%\conf\"
copy /Y "%~dp0src\main\resources\registry.conf" "%SEATA_DIR%\conf\"

REM 提示用户初始化数据库
echo 请确保已经初始化数据库，如果尚未初始化，请执行以下步骤：
echo 1. 打开 MySQL 客户端
echo 2. 执行 %~dp0src\main\resources\db_init.sql 脚本
echo.
echo 是否已初始化数据库？(Y/N)
set /p DB_INIT=

if /i "%DB_INIT%"=="N" (
    echo 请先初始化数据库，然后重新运行此脚本
    pause
    exit /b
)

REM 启动 Seata Server
echo 正在启动 Seata Server...
start "Seata Server" cmd /c "cd /d %SEATA_DIR%\bin && seata-server.bat"

REM 等待 Seata Server 启动
echo 等待 Seata Server 启动...
timeout /t 10 /nobreak

REM 运行测试
echo 正在编译项目...
cd /d %~dp0
call mvn clean compile

echo 运行交互式测试...
echo 1. 运行 Main 类（交互式测试）
echo 2. 运行 TransactionTest 类（自动化测试）
echo 请选择要运行的测试（1/2）：
set /p TEST_CHOICE=

if "%TEST_CHOICE%"=="1" (
    echo 运行 Main 类...
    call mvn exec:java -Dexec.mainClass="org.example.seata.Main"
) else if "%TEST_CHOICE%"=="2" (
    echo 运行 TransactionTest 类...
    call mvn exec:java -Dexec.mainClass="org.example.seata.TransactionTest"
) else (
    echo 无效的选择
)

echo 测试完成
echo 是否关闭 Seata Server？(Y/N)
set /p CLOSE_SERVER=

if /i "%CLOSE_SERVER%"=="Y" (
    echo 正在关闭 Seata Server...
    taskkill /f /fi "WINDOWTITLE eq Seata Server*"
    echo Seata Server 已关闭
)

pause
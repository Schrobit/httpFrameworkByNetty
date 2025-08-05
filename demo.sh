#!/bin/bash

# HttpFramework 演示脚本
# 启动HTTP服务器并测试API端点

echo "=== HttpFramework 演示 ==="
echo "正在启动HTTP服务器..."

# 编译项目
mvn compile

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo "启动服务器在端口 8080..."
    echo "按 Ctrl+C 停止服务器"
    echo ""
    echo "可用的API端点："
    echo "  GET  http://localhost:8080/hello"
    echo "  GET  http://localhost:8080/users/123"
    echo "  GET  http://localhost:8080/search?q=test&page=1"
    echo "  POST http://localhost:8080/users (with JSON body)"
    echo "  GET  http://localhost:8080/custom"
    echo "  GET  http://localhost:8080/error (测试异常处理)"
    echo ""
    
    # 启动服务器
    mvn exec:java -Dexec.mainClass="cn.tjh666.httpframework.Application"
else
    echo "编译失败！"
    exit 1
fi
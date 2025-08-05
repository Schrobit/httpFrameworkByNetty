#!/bin/bash

# HttpFramework 教程示例测试脚本
# 作者: Schrobit
# 邮箱: admin@tjh666.cn

echo "=== HttpFramework 教程示例测试 ==="
echo "确保服务器已在 http://localhost:8080 启动"
echo

# 测试服务器是否启动
echo "1. 检查服务器状态..."
if curl -s http://localhost:8080/hello > /dev/null; then
    echo "✅ 服务器运行正常"
else
    echo "❌ 服务器未启动，请先运行 DemoApplication"
    exit 1
fi
echo

# Hello World 示例测试
echo "2. 测试 Hello World 示例:"
echo "GET /hello"
curl -s http://localhost:8080/hello | jq .
echo

echo "GET /hello/张三"
curl -s http://localhost:8080/hello/张三 | jq .
echo
echo

# 时间服务示例测试
echo "3. 测试时间服务示例:"
echo "GET /time"
curl -s http://localhost:8080/time | jq .
echo

echo "GET /time/formatted?format=yyyy-MM-dd HH:mm:ss"
curl -s "http://localhost:8080/time/formatted?format=yyyy-MM-dd HH:mm:ss" | jq .
echo

echo "GET /time/timezone/Asia/Shanghai"
curl -s http://localhost:8080/time/timezone/Asia/Shanghai | jq .
echo
echo

# 用户管理示例测试
echo "4. 测试用户管理示例:"
echo "GET /users (获取所有用户)"
curl -s http://localhost:8080/users | jq .
echo

echo "GET /users/1 (获取用户1)"
curl -s http://localhost:8080/users/1 | jq .
echo

echo "GET /users/search?q=张 (搜索用户)"
curl -s "http://localhost:8080/users/search?q=张" | jq .
echo

echo "POST /users (创建新用户)"
curl -s -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户","email":"test@example.com","age":"25"}' | jq .
echo

echo "GET /users (验证用户创建)"
curl -s http://localhost:8080/users | jq .
echo

echo "PUT /users/4 (更新用户4)"
curl -s -X PUT http://localhost:8080/users/4 \
  -H "Content-Type: application/json" \
  -d '{"name":"更新的用户","email":"updated@example.com","age":"30"}' | jq .
echo

echo "DELETE /users/4 (删除用户4)"
curl -s -X DELETE http://localhost:8080/users/4 | jq .
echo
echo

# 文件服务示例测试
echo "5. 测试文件服务示例:"
echo "GET /files (获取文件列表)"
curl -s http://localhost:8080/files | jq .
echo

echo "POST /files/upload?filename=test.txt (上传测试文件)"
echo "这是一个测试文件内容\n包含多行文本\n用于演示文件上传功能" | \
curl -s -X POST "http://localhost:8080/files/upload?filename=test.txt" \
  -H "Content-Type: text/plain" \
  --data-binary @- | jq .
echo

echo "GET /files (验证文件上传)"
curl -s http://localhost:8080/files | jq .
echo

# 获取上传的文件名（假设是最新的）
FILENAME=$(curl -s http://localhost:8080/files | jq -r '.files[0].name // ""')
if [ ! -z "$FILENAME" ]; then
    echo "GET /files/$FILENAME (下载文件)"
    curl -s "http://localhost:8080/files/$FILENAME" | jq .
    echo
fi
echo

# 计算器示例测试
echo "6. 测试计算器示例:"
echo "GET /calc/add?a=10&b=5 (加法)"
curl -s "http://localhost:8080/calc/add?a=10&b=5" | jq .
echo

echo "GET /calc/multiply?a=7&b=8 (乘法)"
curl -s "http://localhost:8080/calc/multiply?a=7&b=8" | jq .
echo

echo "GET /calc/divide?a=20&b=4 (除法)"
curl -s "http://localhost:8080/calc/divide?a=20&b=4" | jq .
echo

echo "POST /calc/expression (表达式计算)"
curl -s -X POST http://localhost:8080/calc/expression \
  -H "Content-Type: application/json" \
  -d '{"expression":"15+25"}' | jq .
echo

echo "GET /calc/math/sqrt?x=16 (平方根)"
curl -s "http://localhost:8080/calc/math/sqrt?x=16" | jq .
echo

echo "GET /calc/math/sin?x=1.57 (正弦函数)"
curl -s "http://localhost:8080/calc/math/sin?x=1.57" | jq .
echo
echo

# 错误处理测试
echo "7. 测试错误处理:"
echo "GET /nonexistent (404错误)"
curl -s http://localhost:8080/nonexistent | jq .
echo

echo "GET /users/abc (无效ID)"
curl -s http://localhost:8080/users/abc | jq .
echo

echo "GET /calc/divide?a=10&b=0 (除零错误)"
curl -s "http://localhost:8080/calc/divide?a=10&b=0" | jq .
echo
echo

echo "=== 所有测试完成 ==="
echo "如果看到上述输出，说明HttpFramework的所有示例功能都正常工作！"
echo
echo "您可以使用以下命令进行更多测试:"
echo "curl -X GET http://localhost:8080/hello"
echo "curl -X POST http://localhost:8080/users -d '{\"name\":\"新用户\",\"email\":\"new@example.com\"}'"
echo "curl -X GET 'http://localhost:8080/calc/add?a=100&b=200'"
echo
echo "更多API文档请参考 TUTORIAL.md 文件"
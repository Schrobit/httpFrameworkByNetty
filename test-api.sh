#!/bin/bash

# API测试脚本
# 测试HttpFramework的各种功能

BASE_URL="http://localhost:8080"

echo "=== HttpFramework API 测试 ==="
echo "确保服务器已在 $BASE_URL 启动"
echo ""

# 测试简单GET请求
echo "1. 测试简单GET请求: /hello"
curl -s "$BASE_URL/hello" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/hello"
echo -e "\n"

# 测试路径参数
echo "2. 测试路径参数: /users/123"
curl -s "$BASE_URL/users/123" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/users/123"
echo -e "\n"

# 测试查询参数
echo "3. 测试查询参数: /search?q=java&page=2"
curl -s "$BASE_URL/search?q=java&page=2" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/search?q=java&page=2"
echo -e "\n"

# 测试POST请求
echo "4. 测试POST请求: /users"
curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","age":30,"email":"john@example.com"}' | \
  python3 -m json.tool 2>/dev/null || curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","age":30,"email":"john@example.com"}'
echo -e "\n"

# 测试PUT请求
echo "5. 测试PUT请求: /users/123"
curl -s -X PUT "$BASE_URL/users/123" \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","age":25}' | \
  python3 -m json.tool 2>/dev/null || curl -s -X PUT "$BASE_URL/users/123" \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","age":25}'
echo -e "\n"

# 测试DELETE请求
echo "6. 测试DELETE请求: /users/123"
curl -s -X DELETE "$BASE_URL/users/123" | python3 -m json.tool 2>/dev/null || curl -s -X DELETE "$BASE_URL/users/123"
echo -e "\n"

# 测试自定义响应
echo "7. 测试自定义响应: /custom"
curl -s "$BASE_URL/custom"
echo -e "\n"

# 测试异常处理
echo "8. 测试异常处理: /error"
curl -s "$BASE_URL/error" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/error"
echo -e "\n"

# 测试404错误
echo "9. 测试404错误: /nonexistent"
curl -s "$BASE_URL/nonexistent" || echo "404 Not Found (expected)"
echo -e "\n"

echo "=== API测试完成 ==="
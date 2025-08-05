# HttpFramework 快速开始指南

这是一个5分钟快速上手指南，帮助您立即开始使用HttpFramework。

## 🚀 立即体验

### 1. 启动演示应用

```bash
# 编译项目
mvn compile

# 启动演示服务器
mvn exec:java -Dexec.mainClass="cn.tjh666.demo.DemoApplication"
```

服务器将在 `http://localhost:8080` 启动，您将看到所有可用的API端点。

### 2. 测试基本功能

在浏览器中访问或使用curl测试：

```bash
# Hello World
curl http://localhost:8080/hello
curl http://localhost:8080/hello/张三

# 时间服务
curl http://localhost:8080/time
curl "http://localhost:8080/time/formatted?format=yyyy-MM-dd HH:mm:ss"

# 用户管理
curl http://localhost:8080/users
curl http://localhost:8080/users/1

# 计算器
curl "http://localhost:8080/calc/add?a=10&b=5"
curl "http://localhost:8080/calc/math/sqrt?x=16"
```

### 3. 运行完整测试

```bash
# 运行自动化测试脚本
./test-tutorial-demos.sh
```

## 📝 创建您的第一个API

### 步骤1：创建控制器

```java
package com.example;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.context.Request;
import java.util.HashMap;
import java.util.Map;

public class MyController {
    
    @Get("/api/hello")
    public Map<String, Object> hello(Request request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from my API!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @Get("/api/user/{id}")
    public Map<String, Object> getUser(Request request) {
        String userId = request.getPathParam("id");
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "User " + userId);
        return user;
    }
}
```

### 步骤2：启动服务器

```java
package com.example;

import cn.tjh666.httpframework.server.HttpServer;

public class MyApplication {
    public static void main(String[] args) throws Exception {
        new HttpServer(8080)
            .register(MyController.class)
            .start();
        
        System.out.println("服务器已启动: http://localhost:8080");
    }
}
```

### 步骤3：测试您的API

```bash
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/user/123
```

## 🎯 核心特性示例

### 路径参数
```java
@Get("/users/{id}/posts/{postId}")
public Object getUserPost(Request request) {
    String userId = request.getPathParam("id");
    String postId = request.getPathParam("postId");
    // 处理逻辑
}
```

### 查询参数
```java
@Get("/search")
public Object search(Request request) {
    String query = request.getQueryParam("q");
    String page = request.getQueryParam("page");
    // 处理逻辑
}
```

### POST请求处理
```java
@Post("/users")
public void createUser(Request request, Response response) {
    String jsonBody = request.getBody();
    // 处理创建逻辑
    response.json(201, result);
}
```

### 自定义响应
```java
@Get("/custom")
public void customResponse(Response response) {
    response.json(200, data);    // JSON响应
    response.text(200, "OK");    // 文本响应
    response.html(200, html);    // HTML响应
}
```

## 📚 更多资源

- **完整教程**: 查看 `TUTORIAL.md` 获取详细的使用指南
- **API文档**: 查看 `README.md` 了解所有特性
- **示例代码**: 查看 `src/main/java/cn/tjh666/demo/` 目录下的示例
- **测试脚本**: 运行 `./test-tutorial-demos.sh` 查看完整测试

## 🔧 常用命令

```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 启动演示应用
mvn exec:java -Dexec.mainClass="cn.tjh666.demo.DemoApplication"

# 测试所有功能
./test-tutorial-demos.sh

# 清理项目
mvn clean
```

## 💡 提示

1. **端口冲突**: 如果8080端口被占用，修改代码中的端口号
2. **JSON格式**: 使用 `jq` 工具格式化JSON输出：`curl ... | jq .`
3. **日志查看**: 服务器启动后会显示所有注册的路由
4. **热重载**: 修改代码后需要重启服务器

---

🎉 **恭喜！** 您已经掌握了HttpFramework的基本使用方法。现在可以开始构建您自己的REST API了！

如有问题，请联系：admin@tjh666.cn
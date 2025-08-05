# HttpFramework - 轻量级HTTP框架

基于Netty 4.1+构建的轻量级HTTP框架，专注于RESTful API场景，提供极简、高性能、开发者友好的体验。

## 特性

- ✅ **极简API**: 50行代码内实现基础API服务
- ✅ **高性能**: 基于Netty异步非阻塞架构
- ✅ **零配置**: 无需XML配置文件，注解驱动
- ✅ **HTTP/HTTPS**: 支持HTTP 1.1和HTTPS
- ✅ **路由系统**: 支持路径参数、查询参数
- ✅ **JSON序列化**: 集成Jackson，自动序列化
- ✅ **异常处理**: 统一异常处理机制
- ✅ **轻量级**: 仅依赖Netty和Jackson

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.tjh666</groupId>
    <artifactId>httpFramework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 创建控制器

```java
public class UserController {
    
    @Get("/users/{id}")
    public Map<String, Object> getUser(Request request) {
        String userId = request.getPathParam("id");
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "User " + userId);
        return user;
    }
    
    @Post("/users")
    public Map<String, Object> createUser(Request request) {
        String body = request.getBody();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User created");
        result.put("data", body);
        return result;
    }
}
```

### 3. 启动服务器

```java
public class Application {
    public static void main(String[] args) throws Exception {
        new HttpServer(8080)
            .register(UserController.class)
            .start();
    }
}
```

就这么简单！服务器将在8080端口启动。

## 支持的HTTP方法

- `@Get("/path")` - GET请求
- `@Post("/path")` - POST请求  
- `@Put("/path")` - PUT请求
- `@Delete("/path")` - DELETE请求

## 路径参数

```java
@Get("/users/{id}/posts/{postId}")
public Object getUserPost(Request request) {
    String userId = request.getPathParam("id");
    String postId = request.getPathParam("postId");
    // ...
}
```

## 查询参数

```java
@Get("/search")
public Object search(Request request) {
    String query = request.getQueryParam("q");
    String page = request.getQueryParam("page");
    // ...
}
```

## 请求体处理

```java
@Post("/users")
public Object createUser(Request request) {
    String jsonBody = request.getBody();
    // 手动解析JSON或直接使用
    return result;
}
```

## 自定义响应

```java
@Get("/custom")
public void customResponse(Response response) {
    response.json(201, result);  // JSON响应
    response.text(200, "OK");    // 文本响应
    response.html(200, html);    // HTML响应
}
```

## HTTPS支持

框架提供完整的HTTPS/SSL支持，适用于开发、测试和生产环境：

### 基础HTTPS支持（自签名证书）
适用于开发和测试环境：

```java
// 使用自动生成的自签名证书启动HTTPS服务器
new HttpServer(8443)                    // 标准HTTPS端口
    .enableSsl()                        // 启用SSL，自动生成自签名证书
    .register(Controller.class)         // 注册控制器
    .start();                           // 启动HTTPS服务器
```

### 自定义SSL证书（生产环境）
适用于生产环境，使用真实的SSL证书：

```java
// 加载自定义SSL证书
SslContext sslContext = SslContextBuilder
    .forServer(new File("server.crt"), new File("server.key"))  // 证书和私钥文件
    .build();

new HttpServer(8443)
    .sslContext(sslContext)             // 设置自定义SSL上下文
    .register(Controller.class)
    .start();
```

**注意事项：**
- 自签名证书会导致浏览器显示安全警告
- 生产环境建议使用CA签发的有效证书
- HTTPS默认端口为443，开发环境常用8443

## 异常处理

框架提供默认异常处理器，也可以自定义：

```java
ExceptionMapper customMapper = new ExceptionMapper() {
    @Override
    public void handle(Exception exception, Response response) {
        response.json(500, Map.of("error", exception.getMessage()));
    }
    
    @Override
    public boolean canHandle(Class<? extends Exception> exceptionClass) {
        return true;
    }
};

new HttpServer(8080)
    .exceptionMapper(customMapper)
    .register(Controller.class)
    .start();
```

## 运行测试

```bash
mvn test
```

测试覆盖：
- JSON序列化/反序列化
- 路由匹配和参数提取
- 异常处理
- HTTP服务器集成测试

## 示例项目

运行示例：

```bash
mvn compile exec:java -Dexec.mainClass="cn.tjh666.httpframework.Application"
```

访问示例API：
- GET http://localhost:8080/hello
- GET http://localhost:8080/users/123
- GET http://localhost:8080/search?q=test&page=1
- POST http://localhost:8080/users (with JSON body)

## 技术栈

- **Netty 4.1.121.Final** - 高性能网络框架
- **Jackson 2.15.2** - JSON序列化
- **JUnit 5** - 单元测试
- **Java 8+** - 最低Java版本要求

## 性能特点

- 基于Netty的异步非阻塞I/O
- 零拷贝网络传输
- 高并发连接支持
- 低内存占用
- 快速启动时间

## 许可证

MIT License

## 作者

- **Schrobit** - admin@tjh666.cn

---

**HttpFramework** - 让REST API开发变得简单而高效！
# 任务：设计一个轻量级HTTP框架（httpFramework）用于构建高性能REST API
## 背景
- 底层依赖 Netty 4.1+，目标替代传统 Servlet 容器，专注 RESTful API 场景。
- 核心诉求：**极简、高性能、开发者友好**，50行代码内实现基础API服务。

## 技术规范
### 1. 核心能力
- [必须] 支持 HTTP 1.1/2，自动处理 GET/POST/PUT/DELETE 方法
- [必须] 支持HTTPS
- [必须] 路由系统：通过注解（如 `@Get("/users")`）或链式 DSL 定义端点
- [必须] 请求/响应封装：`Request` 对象提供参数解析（Query/Path/Body），`Response` 支持 JSON/文本返回
- [必须] 异常处理：全局 `ExceptionMapper` 统一错误格式

### 3. 开发者体验
- [必须] 零配置启动：`new HttpServer(8080).register(Controllers.class).start()`
- [必须] 自动 JSON 序列化：集成 Jackson，`response.json(200, obj)` 直接输出
- [禁止] 不引入 Spring 等重量级依赖，仅依赖：
- `io.netty:netty-all:4.1.121.Final`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`

### 4. 代码结构
- 模块化设计：
- `server/`：Netty 服务启动器（`HttpServer`）
- `routing/`：路由注册器（`Router`）、注解处理器
- `context/`：`Request`/`Response` 上下文封装
- 路由通过显式注册

### 5. 交付物要求通过 JUnit 验证：单测覆盖路由匹配、异常处理、JSON 序列化
- 通过 JUnit 验证：单测覆盖路由匹配、异常处理、JSON 序列化

### 6. 项目结构
httpFramework/
├── server/           # Netty 启动器、ServerBootstrap 封装
├── routing/          # 路由注册器、注解定义（@Get, @Post...）
├── handler/          # 请求处理逻辑（如 Controller 调用）
├── context/          # Request/Response 封装
├── exception/        # ExceptionMapper 接口与默认实现
├── json/             # JSON 序列化封装（Jackson 集成）
├── annotation/       # 自定义注解定义
└── util/             # 工具类
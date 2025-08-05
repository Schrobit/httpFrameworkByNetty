# HttpFramework 完整使用教程

本教程将带您从零开始学习HttpFramework的使用，通过丰富的示例帮助您快速掌握这个轻量级HTTP框架。

## 目录

1. [环境准备](#环境准备)
2. [快速入门](#快速入门)
3. [基础示例](#基础示例)
4. [进阶功能](#进阶功能)
5. [最佳实践](#最佳实践)
6. [常见问题](#常见问题)

## 环境准备

### 系统要求

- Java 8 或更高版本
- Maven 3.6 或更高版本

### 项目依赖

在您的 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>cn.tjh666</groupId>
        <artifactId>httpFramework</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## 快速入门

### 第一个HTTP服务

让我们从最简单的"Hello World"开始：

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.Application;
import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.server.HttpServer;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的Hello World示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class HelloWorldDemo {
    
    /**
     * 简单的问候接口
     * @param request HTTP请求对象
     * @return 问候消息
     */
    @Get("/hello")
    public Map<String, Object> hello(Request request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, HttpFramework!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return response;
    }
    
    /**
     * 个性化问候接口
     * @param request HTTP请求对象
     * @return 个性化问候消息
     */
    @Get("/hello/{name}")
    public Map<String, Object> helloWithName(Request request) {
        String name = request.getPathParam("name");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    public static void main(String[] args) throws Exception {
        new HttpServer(8080)
            .register(HelloWorldDemo.class)
            .start();
        
        System.out.println("服务器已启动，访问: http://localhost:8080/hello");
    }
}
```

### 运行和测试

1. 编译并运行上述代码
2. 在浏览器中访问 `http://localhost:8080/hello`
3. 尝试访问 `http://localhost:8080/hello/张三`

## 基础示例

### 示例1：时间服务 (/time)

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间服务示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class TimeController {
    
    /**
     * 获取当前时间（JSON格式）
     * @param request HTTP请求对象
     * @return 时间信息
     */
    @Get("/time")
    public Map<String, Object> getCurrentTime(Request request) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> timeInfo = new HashMap<>();
        
        timeInfo.put("timestamp", System.currentTimeMillis());
        timeInfo.put("datetime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        timeInfo.put("date", now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        timeInfo.put("time", now.format(DateTimeFormatter.ISO_LOCAL_TIME));
        timeInfo.put("year", now.getYear());
        timeInfo.put("month", now.getMonthValue());
        timeInfo.put("day", now.getDayOfMonth());
        timeInfo.put("hour", now.getHour());
        timeInfo.put("minute", now.getMinute());
        timeInfo.put("second", now.getSecond());
        
        return timeInfo;
    }
    
    /**
     * 获取格式化时间
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/time/formatted")
    public void getFormattedTime(Request request, Response response) {
        String format = request.getQueryParam("format");
        LocalDateTime now = LocalDateTime.now();
        
        String formattedTime;
        if (format != null && !format.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                formattedTime = now.format(formatter);
            } catch (Exception e) {
                response.json(400, Map.of("error", "无效的时间格式: " + format));
                return;
            }
        } else {
            formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        response.json(200, Map.of("formatted_time", formattedTime));
    }
    
    /**
     * 获取时区时间
     * @param request HTTP请求对象
     * @return 时区时间信息
     */
    @Get("/time/timezone/{zone}")
    public Map<String, Object> getTimeByZone(Request request) {
        String zone = request.getPathParam("zone");
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里简化处理，实际应用中可以使用ZoneId
            LocalDateTime now = LocalDateTime.now();
            result.put("zone", zone);
            result.put("local_time", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            result.put("note", "时区功能需要进一步实现");
        } catch (Exception e) {
            result.put("error", "无效的时区: " + zone);
        }
        
        return result;
    }
}
```

### 示例2：用户管理API

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.*;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户管理API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class UserController {
    
    // 模拟数据库存储
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    static {
        // 初始化一些测试数据
        users.put(1L, new User(1L, "张三", "zhangsan@example.com", 25));
        users.put(2L, new User(2L, "李四", "lisi@example.com", 30));
        users.put(3L, new User(3L, "王五", "wangwu@example.com", 28));
        idGenerator.set(4);
    }
    
    /**
     * 获取所有用户
     * @param request HTTP请求对象
     * @return 用户列表
     */
    @Get("/users")
    public Map<String, Object> getAllUsers(Request request) {
        String pageStr = request.getQueryParam("page");
        String sizeStr = request.getQueryParam("size");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
        
        List<User> userList = new ArrayList<>(users.values());
        int start = (page - 1) * size;
        int end = Math.min(start + size, userList.size());
        
        List<User> pageUsers = start < userList.size() ? 
            userList.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", pageUsers);
        result.put("total", users.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (users.size() + size - 1) / size);
        
        return result;
    }
    
    /**
     * 根据ID获取用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/users/{id}")
    public void getUserById(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User user = users.get(id);
            
            if (user != null) {
                response.json(200, user);
            } else {
                response.json(404, Map.of("error", "用户不存在", "id", id));
            }
        } catch (NumberFormatException e) {
            response.json(400, Map.of("error", "无效的用户ID"));
        }
    }
    
    /**
     * 创建新用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/users")
    public void createUser(Request request, Response response) {
        try {
            String body = request.getBody();
            if (body == null || body.trim().isEmpty()) {
                response.json(400, Map.of("error", "请求体不能为空"));
                return;
            }
            
            // 简单的JSON解析（实际项目中建议使用Jackson）
            Map<String, Object> userData = parseSimpleJson(body);
            
            String name = (String) userData.get("name");
            String email = (String) userData.get("email");
            Integer age = userData.get("age") != null ? 
                Integer.parseInt(userData.get("age").toString()) : null;
            
            if (name == null || email == null) {
                response.json(400, Map.of("error", "姓名和邮箱是必填项"));
                return;
            }
            
            Long id = idGenerator.getAndIncrement();
            User newUser = new User(id, name, email, age);
            users.put(id, newUser);
            
            response.json(201, Map.of(
                "message", "用户创建成功",
                "user", newUser
            ));
            
        } catch (Exception e) {
            response.json(400, Map.of("error", "创建用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户信息
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Put("/users/{id}")
    public void updateUser(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User existingUser = users.get(id);
            
            if (existingUser == null) {
                response.json(404, Map.of("error", "用户不存在", "id", id));
                return;
            }
            
            String body = request.getBody();
            Map<String, Object> updateData = parseSimpleJson(body);
            
            // 更新用户信息
            String name = (String) updateData.get("name");
            String email = (String) updateData.get("email");
            Integer age = updateData.get("age") != null ? 
                Integer.parseInt(updateData.get("age").toString()) : null;
            
            User updatedUser = new User(
                id,
                name != null ? name : existingUser.getName(),
                email != null ? email : existingUser.getEmail(),
                age != null ? age : existingUser.getAge()
            );
            
            users.put(id, updatedUser);
            
            response.json(200, Map.of(
                "message", "用户更新成功",
                "user", updatedUser
            ));
            
        } catch (Exception e) {
            response.json(400, Map.of("error", "更新用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Delete("/users/{id}")
    public void deleteUser(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User deletedUser = users.remove(id);
            
            if (deletedUser != null) {
                response.json(200, Map.of(
                    "message", "用户删除成功",
                    "user", deletedUser
                ));
            } else {
                response.json(404, Map.of("error", "用户不存在", "id", id));
            }
        } catch (NumberFormatException e) {
            response.json(400, Map.of("error", "无效的用户ID"));
        }
    }
    
    /**
     * 搜索用户
     * @param request HTTP请求对象
     * @return 搜索结果
     */
    @Get("/users/search")
    public Map<String, Object> searchUsers(Request request) {
        String keyword = request.getQueryParam("q");
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Map.of("users", new ArrayList<>(), "total", 0);
        }
        
        List<User> matchedUsers = users.values().stream()
            .filter(user -> user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                           user.getEmail().toLowerCase().contains(keyword.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        return Map.of(
            "users", matchedUsers,
            "total", matchedUsers.size(),
            "keyword", keyword
        );
    }
    
    /**
     * 简单的JSON解析方法（仅用于演示）
     * @param json JSON字符串
     * @return 解析后的Map
     */
    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        // 这里是一个非常简单的JSON解析实现，仅用于演示
        // 实际项目中应该使用Jackson等专业库
        json = json.trim().replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }
        
        return result;
    }
    
    /**
     * 用户实体类
     */
    public static class User {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        
        public User(Long id, String name, String email, Integer age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Integer getAge() { return age; }
        
        // Setters
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setAge(Integer age) { this.age = age; }
    }
}
```

### 示例3：文件服务API

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.annotation.Post;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件服务API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class FileController {
    
    private static final String UPLOAD_DIR = "uploads";
    
    static {
        // 确保上传目录存在
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件列表
     * @param request HTTP请求对象
     * @return 文件列表
     */
    @Get("/files")
    public Map<String, Object> listFiles(Request request) {
        try {
            List<Map<String, Object>> files = new ArrayList<>();
            
            Files.list(Paths.get(UPLOAD_DIR))
                .forEach(path -> {
                    try {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("name", path.getFileName().toString());
                        fileInfo.put("size", Files.size(path));
                        fileInfo.put("lastModified", Files.getLastModifiedTime(path).toString());
                        fileInfo.put("isDirectory", Files.isDirectory(path));
                        files.add(fileInfo);
                    } catch (IOException e) {
                        // 忽略单个文件的错误
                    }
                });
            
            return Map.of(
                "files", files,
                "total", files.size(),
                "uploadDir", UPLOAD_DIR
            );
            
        } catch (IOException e) {
            return Map.of("error", "读取文件列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件内容
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/files/{filename}")
    public void getFile(Request request, Response response) {
        String filename = request.getPathParam("filename");
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        
        try {
            if (!Files.exists(filePath)) {
                response.json(404, Map.of("error", "文件不存在: " + filename));
                return;
            }
            
            if (Files.isDirectory(filePath)) {
                response.json(400, Map.of("error", "不能下载目录: " + filename));
                return;
            }
            
            // 读取文件内容
            String content = Files.readString(filePath);
            
            Map<String, Object> fileData = new HashMap<>();
            fileData.put("filename", filename);
            fileData.put("content", content);
            fileData.put("size", Files.size(filePath));
            fileData.put("lastModified", Files.getLastModifiedTime(filePath).toString());
            
            response.json(200, fileData);
            
        } catch (IOException e) {
            response.json(500, Map.of("error", "读取文件失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传文件（简单文本文件）
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/files/upload")
    public void uploadFile(Request request, Response response) {
        try {
            String filename = request.getQueryParam("filename");
            String content = request.getBody();
            
            if (filename == null || filename.trim().isEmpty()) {
                response.json(400, Map.of("error", "文件名不能为空"));
                return;
            }
            
            if (content == null) {
                response.json(400, Map.of("error", "文件内容不能为空"));
                return;
            }
            
            // 生成唯一文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueFilename = timestamp + "_" + filename;
            
            Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);
            Files.writeString(filePath, content);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "文件上传成功");
            result.put("filename", uniqueFilename);
            result.put("originalName", filename);
            result.put("size", content.length());
            result.put("uploadTime", LocalDateTime.now().toString());
            
            response.json(201, result);
            
        } catch (IOException e) {
            response.json(500, Map.of("error", "文件上传失败: " + e.getMessage()));
        }
    }
}
```

### 示例4：计算器API

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.annotation.Post;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算器API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class CalculatorController {
    
    /**
     * 基本算术运算
     * @param request HTTP请求对象
     * @return 计算结果
     */
    @Get("/calc/{operation}")
    public Map<String, Object> calculate(Request request) {
        String operation = request.getPathParam("operation");
        String aStr = request.getQueryParam("a");
        String bStr = request.getQueryParam("b");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (aStr == null || bStr == null) {
                result.put("error", "参数a和b是必需的");
                return result;
            }
            
            double a = Double.parseDouble(aStr);
            double b = Double.parseDouble(bStr);
            double calcResult;
            
            switch (operation.toLowerCase()) {
                case "add":
                    calcResult = a + b;
                    break;
                case "subtract":
                    calcResult = a - b;
                    break;
                case "multiply":
                    calcResult = a * b;
                    break;
                case "divide":
                    if (b == 0) {
                        result.put("error", "除数不能为零");
                        return result;
                    }
                    calcResult = a / b;
                    break;
                case "power":
                    calcResult = Math.pow(a, b);
                    break;
                default:
                    result.put("error", "不支持的运算: " + operation);
                    result.put("supported", new String[]{"add", "subtract", "multiply", "divide", "power"});
                    return result;
            }
            
            result.put("operation", operation);
            result.put("a", a);
            result.put("b", b);
            result.put("result", calcResult);
            result.put("expression", a + " " + getOperationSymbol(operation) + " " + b + " = " + calcResult);
            
        } catch (NumberFormatException e) {
            result.put("error", "无效的数字格式");
        }
        
        return result;
    }
    
    /**
     * 复杂表达式计算
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/calc/expression")
    public void evaluateExpression(Request request, Response response) {
        try {
            String body = request.getBody();
            Map<String, Object> requestData = parseSimpleJson(body);
            String expression = (String) requestData.get("expression");
            
            if (expression == null || expression.trim().isEmpty()) {
                response.json(400, Map.of("error", "表达式不能为空"));
                return;
            }
            
            // 简单的表达式计算（仅支持基本运算）
            double result = evaluateSimpleExpression(expression);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("expression", expression);
            responseData.put("result", result);
            responseData.put("note", "仅支持简单的四则运算");
            
            response.json(200, responseData);
            
        } catch (Exception e) {
            response.json(400, Map.of("error", "表达式计算失败: " + e.getMessage()));
        }
    }
    
    /**
     * 数学函数计算
     * @param request HTTP请求对象
     * @return 计算结果
     */
    @Get("/calc/math/{function}")
    public Map<String, Object> mathFunction(Request request) {
        String function = request.getPathParam("function");
        String xStr = request.getQueryParam("x");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (xStr == null) {
                result.put("error", "参数x是必需的");
                return result;
            }
            
            double x = Double.parseDouble(xStr);
            double calcResult;
            
            switch (function.toLowerCase()) {
                case "sin":
                    calcResult = Math.sin(x);
                    break;
                case "cos":
                    calcResult = Math.cos(x);
                    break;
                case "tan":
                    calcResult = Math.tan(x);
                    break;
                case "sqrt":
                    if (x < 0) {
                        result.put("error", "负数不能开平方根");
                        return result;
                    }
                    calcResult = Math.sqrt(x);
                    break;
                case "log":
                    if (x <= 0) {
                        result.put("error", "对数的真数必须大于0");
                        return result;
                    }
                    calcResult = Math.log(x);
                    break;
                case "log10":
                    if (x <= 0) {
                        result.put("error", "对数的真数必须大于0");
                        return result;
                    }
                    calcResult = Math.log10(x);
                    break;
                case "abs":
                    calcResult = Math.abs(x);
                    break;
                case "ceil":
                    calcResult = Math.ceil(x);
                    break;
                case "floor":
                    calcResult = Math.floor(x);
                    break;
                default:
                    result.put("error", "不支持的数学函数: " + function);
                    result.put("supported", new String[]{"sin", "cos", "tan", "sqrt", "log", "log10", "abs", "ceil", "floor"});
                    return result;
            }
            
            result.put("function", function);
            result.put("x", x);
            result.put("result", calcResult);
            result.put("expression", function + "(" + x + ") = " + calcResult);
            
        } catch (NumberFormatException e) {
            result.put("error", "无效的数字格式");
        }
        
        return result;
    }
    
    /**
     * 获取运算符号
     * @param operation 运算名称
     * @return 运算符号
     */
    private String getOperationSymbol(String operation) {
        switch (operation.toLowerCase()) {
            case "add": return "+";
            case "subtract": return "-";
            case "multiply": return "*";
            case "divide": return "/";
            case "power": return "^";
            default: return operation;
        }
    }
    
    /**
     * 简单表达式计算（仅用于演示）
     * @param expression 表达式
     * @return 计算结果
     */
    private double evaluateSimpleExpression(String expression) {
        // 这是一个非常简单的实现，仅支持基本的四则运算
        // 实际项目中应该使用专业的表达式解析库
        expression = expression.replaceAll("\\s+", "");
        
        // 简单处理加法
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
        }
        
        // 简单处理减法
        if (expression.contains("-") && !expression.startsWith("-")) {
            String[] parts = expression.split("-");
            return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
        }
        
        // 简单处理乘法
        if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
        }
        
        // 简单处理除法
        if (expression.contains("/")) {
            String[] parts = expression.split("/");
            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
        }
        
        // 如果没有运算符，直接返回数字
        return Double.parseDouble(expression);
    }
    
    /**
     * 简单的JSON解析方法
     * @param json JSON字符串
     * @return 解析后的Map
     */
    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim().replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }
        
        return result;
    }
}
```

## 进阶功能

### 完整的应用示例

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.server.HttpServer;

/**
 * 完整的演示应用
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class DemoApplication {
    
    public static void main(String[] args) throws Exception {
        // 创建HTTP服务器并注册所有控制器
        HttpServer server = new HttpServer(8080)
            .register(HelloWorldDemo.class)
            .register(TimeController.class)
            .register(UserController.class)
            .register(FileController.class)
            .register(CalculatorController.class);
        
        // 启动服务器
        server.start();
        
        System.out.println("=== HttpFramework 演示应用已启动 ===");
        System.out.println("服务器地址: http://localhost:8080");
        System.out.println();
        System.out.println("可用的API端点:");
        System.out.println("1. Hello World:");
        System.out.println("   GET  /hello");
        System.out.println("   GET  /hello/{name}");
        System.out.println();
        System.out.println("2. 时间服务:");
        System.out.println("   GET  /time");
        System.out.println("   GET  /time/formatted?format=yyyy-MM-dd");
        System.out.println("   GET  /time/timezone/{zone}");
        System.out.println();
        System.out.println("3. 用户管理:");
        System.out.println("   GET    /users");
        System.out.println("   GET    /users/{id}");
        System.out.println("   POST   /users");
        System.out.println("   PUT    /users/{id}");
        System.out.println("   DELETE /users/{id}");
        System.out.println("   GET    /users/search?q=keyword");
        System.out.println();
        System.out.println("4. 文件服务:");
        System.out.println("   GET  /files");
        System.out.println("   GET  /files/{filename}");
        System.out.println("   POST /files/upload?filename=test.txt");
        System.out.println();
        System.out.println("5. 计算器:");
        System.out.println("   GET  /calc/{operation}?a=10&b=5");
        System.out.println("   POST /calc/expression");
        System.out.println("   GET  /calc/math/{function}?x=3.14");
        System.out.println();
        System.out.println("按 Ctrl+C 停止服务器");
    }
}
```

### 自定义异常处理

```java
package cn.tjh666.demo;

import cn.tjh666.httpframework.context.Response;
import cn.tjh666.httpframework.exception.ExceptionMapper;
import cn.tjh666.httpframework.server.HttpServer;

import java.util.Map;

/**
 * 自定义异常处理示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class CustomExceptionDemo {
    
    /**
     * 自定义异常处理器
     */
    public static class CustomExceptionMapper implements ExceptionMapper {
        
        @Override
        public void handle(Exception exception, Response response) {
            if (exception instanceof IllegalArgumentException) {
                response.json(400, Map.of(
                    "error", "参数错误",
                    "message", exception.getMessage(),
                    "type", "INVALID_ARGUMENT"
                ));
            } else if (exception instanceof SecurityException) {
                response.json(403, Map.of(
                    "error", "权限不足",
                    "message", exception.getMessage(),
                    "type", "ACCESS_DENIED"
                ));
            } else {
                response.json(500, Map.of(
                    "error", "服务器内部错误",
                    "message", exception.getMessage(),
                    "type", "INTERNAL_ERROR"
                ));
            }
        }
        
        @Override
        public boolean canHandle(Class<? extends Exception> exceptionClass) {
            return true; // 处理所有异常
        }
    }
    
    public static void main(String[] args) throws Exception {
        new HttpServer(8080)
            .exceptionMapper(new CustomExceptionMapper())
            .register(HelloWorldDemo.class)
            .start();
    }
}
```

## 最佳实践

### 1. 项目结构建议

```
src/main/java/
├── cn/tjh666/demo/
│   ├── Application.java          # 主启动类
│   ├── controller/               # 控制器包
│   │   ├── UserController.java
│   │   ├── FileController.java
│   │   └── ...
│   ├── service/                  # 业务逻辑包
│   │   ├── UserService.java
│   │   └── ...
│   ├── model/                    # 数据模型包
│   │   ├── User.java
│   │   └── ...
│   ├── exception/                # 自定义异常包
│   │   ├── CustomExceptionMapper.java
│   │   └── ...
│   └── util/                     # 工具类包
│       └── ...
```

### 2. 错误处理建议

- 总是验证输入参数
- 使用适当的HTTP状态码
- 提供清晰的错误消息
- 记录异常信息（用于调试）

### 3. 性能优化建议

- 避免在请求处理中进行耗时操作
- 使用连接池管理数据库连接
- 合理设置线程池大小
- 启用GZIP压缩（如需要）

### 4. 安全建议

- 验证所有输入数据
- 使用HTTPS传输敏感数据
- 实现适当的认证和授权机制
- 防止SQL注入和XSS攻击

## 常见问题

### Q1: 如何处理大文件上传？

A: 当前示例仅支持小文件。对于大文件，建议：
- 使用流式处理
- 实现分块上传
- 设置合适的超时时间
- 考虑使用专门的文件存储服务

### Q2: 如何实现数据库集成？

A: 可以在Service层集成数据库：
```java
// 添加数据库依赖（如MySQL、H2等）
// 在Service类中使用JDBC或ORM框架
public class UserService {
    public User findById(Long id) {
        // 数据库查询逻辑
    }
}
```

### Q3: 如何实现认证和授权？

A: 可以通过拦截器或过滤器实现：
```java
// 在请求处理前检查认证信息
public boolean isAuthenticated(Request request) {
    String token = request.getHeader("Authorization");
    return validateToken(token);
}
```

### Q4: 如何配置CORS？

A: 在响应中添加CORS头：
```java
response.setHeader("Access-Control-Allow-Origin", "*");
response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
```

### Q5: 如何进行单元测试？

A: 使用JUnit和Mock框架：
```java
@Test
public void testGetUser() {
    // 创建模拟请求
    // 调用控制器方法
    // 验证响应结果
}
```

## 总结

HttpFramework提供了一个轻量级、高性能的HTTP服务开发解决方案。通过本教程的示例，您可以：

1. 快速搭建REST API服务
2. 处理各种HTTP请求和响应
3. 实现文件上传下载
4. 构建计算服务
5. 管理用户数据

框架的设计理念是简单易用，让开发者能够专注于业务逻辑而不是底层网络处理。随着项目的发展，您可以根据需要扩展功能，如添加数据库支持、认证授权、缓存等。

希望这个教程能帮助您快速上手HttpFramework！如有问题，请联系 admin@tjh666.cn。
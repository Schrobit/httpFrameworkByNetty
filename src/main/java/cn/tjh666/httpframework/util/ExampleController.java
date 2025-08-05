package cn.tjh666.httpframework.util;

import cn.tjh666.httpframework.annotation.*;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例控制器
 * 展示框架的基本使用方法
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class ExampleController {

    /**
     * 简单的GET请求处理
     * @return 响应数据
     */
    @Get("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, HttpFramework!");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 带路径参数的GET请求
     * @param request 请求对象
     * @return 响应数据
     */
    @Get("/users/{id}")
    public Map<String, Object> getUser(Request request) {
        String userId = request.getPathParam("id");
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "User " + userId);
        user.put("email", "user" + userId + "@example.com");
        
        return user;
    }

    /**
     * 带查询参数的GET请求
     * @param request 请求对象
     * @return 响应数据
     */
    @Get("/search")
    public Map<String, Object> search(Request request) {
        String query = request.getQueryParam("q");
        String page = request.getQueryParam("page");
        
        Map<String, Object> result = new HashMap<>();
        result.put("query", query != null ? query : "");
        result.put("page", page != null ? Integer.parseInt(page) : 1);
        result.put("results", "Search results for: " + query);
        
        return result;
    }

    /**
     * POST请求处理
     * @param request 请求对象
     * @return 响应数据
     */
    @Post("/users")
    public Map<String, Object> createUser(Request request) {
        String body = request.getBody();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User created successfully");
        result.put("data", body);
        result.put("id", System.currentTimeMillis());
        
        return result;
    }

    /**
     * PUT请求处理
     * @param request 请求对象
     * @return 响应数据
     */
    @Put("/users/{id}")
    public Map<String, Object> updateUser(Request request) {
        String userId = request.getPathParam("id");
        String body = request.getBody();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User " + userId + " updated successfully");
        result.put("data", body);
        
        return result;
    }

    /**
     * DELETE请求处理
     * @param request 请求对象
     * @return 响应数据
     */
    @Delete("/users/{id}")
    public Map<String, Object> deleteUser(Request request) {
        String userId = request.getPathParam("id");
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User " + userId + " deleted successfully");
        
        return result;
    }

    /**
     * 直接操作Response对象的示例
     * @param response 响应对象
     */
    @Get("/custom")
    public void customResponse(Response response) {
        response.text(200, "This is a custom text response");
    }

    /**
     * 抛出异常的示例
     * @return 不会返回，会抛出异常
     */
    @Get("/error")
    public Map<String, Object> errorExample() {
        throw new RuntimeException("This is a test exception");
    }
}
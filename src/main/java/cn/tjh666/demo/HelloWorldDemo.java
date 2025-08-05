package cn.tjh666.demo;

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
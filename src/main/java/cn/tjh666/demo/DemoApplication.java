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
        System.out.println("测试脚本: ./test-tutorial-demos.sh");
        System.out.println("按 Ctrl+C 停止服务器");
    }
}
package cn.tjh666.httpframework;

import cn.tjh666.httpframework.server.HttpServer;
import cn.tjh666.httpframework.util.ExampleController;

/**
 * 应用程序启动类
 * 展示框架的极简使用方式 - 50行代码内实现基础API服务
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class Application {
    
    /**
     * 主方法 - 启动HTTP服务器
     * @param args 命令行参数
     */
    // public static void main(String[] args) {
    //     try {
    //         // 创建并启动HTTP服务器 - 仅需3行代码！
    //         new HttpServer(8080)
    //             .register(ExampleController.class)
    //             .start();
                
    //     } catch (Exception e) {
    //         System.err.println("Failed to start server: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
    public static void main(String[] args) {
        try {
            new HttpServer(8443)          // HTTPS标准端口
                .enableSsl()              // 启用SSL/TLS支持，自动生成自签名证书
                .register(ExampleController.class)  // 注册控制器
                .start();                 // 启动HTTPS服务器
                
        } catch (Exception e) {
            System.err.println("Failed to start HTTPS server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 启动HTTPS服务器的示例
     * 演示如何启用SSL/TLS支持，创建HTTPS服务器
     * 
     * 特点：
     * 1. 使用标准HTTPS端口8443
     * 2. 通过enableSsl()启用SSL支持
     * 3. 框架自动生成自签名证书（适用于开发测试）
     * 4. 生产环境建议使用sslContext()方法配置真实证书
     * 
     * 访问方式：https://localhost:8443/hello
     * 注意：浏览器会提示证书不受信任（因为是自签名证书）
     */
    public static void startHttpsServer() {
        try {
            new HttpServer(8443)          // HTTPS标准端口
                .enableSsl()              // 启用SSL/TLS支持，自动生成自签名证书
                .register(ExampleController.class)  // 注册控制器
                .start();                 // 启动HTTPS服务器
                
        } catch (Exception e) {
            System.err.println("Failed to start HTTPS server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
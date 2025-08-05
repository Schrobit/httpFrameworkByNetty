package cn.tjh666.httpframework.server;

import cn.tjh666.httpframework.exception.DefaultExceptionMapper;
import cn.tjh666.httpframework.exception.ExceptionMapper;
import cn.tjh666.httpframework.handler.HttpRequestHandler;
import cn.tjh666.httpframework.json.JsonSerializer;
import cn.tjh666.httpframework.routing.Router;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
// HTTPS/SSL支持相关导入
import io.netty.handler.ssl.SslContext;           // SSL上下文，用于配置SSL/TLS参数
import io.netty.handler.ssl.SslContextBuilder;    // SSL上下文构建器，用于创建SSL配置
import io.netty.handler.ssl.util.SelfSignedCertificate; // 自签名证书工具类，用于开发和测试环境

/**
 * HTTP服务器
 * 基于Netty实现的轻量级HTTP服务器
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class HttpServer {
    private final int port;
    private final Router router;
    private final JsonSerializer jsonSerializer;
    private ExceptionMapper exceptionMapper;
    
    // HTTPS/SSL配置相关字段
    private boolean sslEnabled = false;    // SSL启用标志，默认为false（HTTP模式）
    private SslContext sslContext;         // SSL上下文对象，包含证书、私钥等SSL配置信息
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    /**
     * 构造HTTP服务器
     * @param port 监听端口
     */
    public HttpServer(int port) {
        this.port = port;
        this.router = new Router();
        this.jsonSerializer = new JsonSerializer();
        this.exceptionMapper = new DefaultExceptionMapper();
    }

    /**
     * 注册控制器类
     * @param controllerClass 控制器类
     * @return 服务器实例，支持链式调用
     */
    public HttpServer register(Class<?> controllerClass) {
        router.register(controllerClass);
        return this;
    }

    /**
     * 设置自定义异常处理器
     * @param exceptionMapper 异常处理器
     * @return 服务器实例，支持链式调用
     */
    public HttpServer exceptionMapper(ExceptionMapper exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
        return this;
    }

    /**
     * 启用HTTPS支持
     * 调用此方法后，服务器将使用HTTPS协议而非HTTP协议
     * 如果未通过sslContext()方法设置自定义SSL上下文，
     * 框架将自动生成自签名证书用于开发和测试
     * 
     * @return 服务器实例，支持链式调用
     */
    public HttpServer enableSsl() {
        this.sslEnabled = true;
        return this;
    }

    /**
     * 设置自定义SSL上下文
     * 允许开发者提供自己的SSL证书和配置，用于生产环境
     * 调用此方法会自动启用SSL支持
     * 
     * 使用示例：
     * SslContext sslCtx = SslContextBuilder.forServer(certFile, keyFile).build();
     * server.sslContext(sslCtx);
     * 
     * @param sslContext 预配置的SSL上下文，包含证书、私钥等信息
     * @return 服务器实例，支持链式调用
     */
    public HttpServer sslContext(SslContext sslContext) {
        this.sslContext = sslContext;
        this.sslEnabled = true;  // 设置SSL上下文时自动启用SSL
        return this;
    }

    /**
     * 启动服务器（同步模式）
     * 根据SSL配置启动HTTP或HTTPS服务器
     * @throws Exception 启动异常
     */
    public void start() throws Exception {
        // SSL证书自动配置逻辑
        // 如果启用了SSL但没有设置自定义SSL上下文，则自动生成自签名证书
        // 这种方式适用于开发和测试环境，生产环境建议使用真实证书
        if (sslEnabled && sslContext == null) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();  // 生成自签名证书
            // 使用自签名证书创建SSL上下文，包含公钥证书和私钥
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // HTTPS/SSL处理器配置
                            // SSL处理器必须放在管道的最前面，用于处理TLS握手和加密/解密
                            if (sslEnabled && sslContext != null) {
                                // 为每个连接创建SSL处理器实例，处理SSL/TLS协议
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }
                            
                            // 添加HTTP编解码器（处理HTTP协议，位于SSL层之上）
                            pipeline.addLast(new HttpServerCodec());
                            
                            // 添加HTTP对象聚合器，将HTTP消息聚合为FullHttpRequest
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            
                            // 添加自定义请求处理器
                            pipeline.addLast(new HttpRequestHandler(router, jsonSerializer, exceptionMapper));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            
            // 根据SSL配置显示协议类型
            String protocol = sslEnabled ? "HTTPS" : "HTTP";
            System.out.println(protocol + " Server started on port " + port);
            System.out.println("Registered routes:");
            router.getRoutes().forEach(route -> System.out.println("  " + route));
            
            // 等待服务器关闭
            serverChannel.closeFuture().sync();
        } finally {
            shutdown();  // 正确处理资源释放
        }
    }

    /**
     * 异步启动服务器
     * 与start()方法不同，此方法不会阻塞当前线程
     * 根据SSL配置启动HTTP或HTTPS服务器
     * @return ChannelFuture对象，可用于监听服务器启动状态
     * @throws Exception 启动异常
     */
    public ChannelFuture startAsync() throws Exception {
        // SSL证书自动配置逻辑（与同步启动相同）
        // 如果启用了SSL但没有设置自定义SSL上下文，则自动生成自签名证书
        if (sslEnabled && sslContext == null) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();  // 生成自签名证书
            // 使用自签名证书创建SSL上下文，包含公钥证书和私钥
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // HTTPS/SSL处理器配置（与同步启动相同的管道配置）
                        // SSL处理器必须放在管道的最前面，用于处理TLS握手和加密/解密
                        if (sslEnabled && sslContext != null) {
                            // 为每个连接创建SSL处理器实例，处理SSL/TLS协议
                            pipeline.addLast(sslContext.newHandler(ch.alloc()));
                        }
                        
                        // 添加HTTP编解码器（处理HTTP协议，位于SSL层之上）
                        pipeline.addLast(new HttpServerCodec());
                        
                        // 添加HTTP对象聚合器
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        
                        // 添加自定义请求处理器
                        pipeline.addLast(new HttpRequestHandler(router, jsonSerializer, exceptionMapper));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定端口
        ChannelFuture future = bootstrap.bind(port);
        serverChannel = future.channel();
        
        // 根据SSL配置显示协议类型（异步启动时显示"starting"而非"started"）
        String protocol = sslEnabled ? "HTTPS" : "HTTP";
        System.out.println(protocol + " Server starting on port " + port);
        System.out.println("Registered routes:");
        router.getRoutes().forEach(route -> System.out.println("  " + route));
        
        return future;
    }

    /**
     * 关闭服务器
     */
    public void shutdown() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 获取路由器
     * @return 路由器实例
     */
    public Router getRouter() {
        return router;
    }

    /**
     * 获取JSON序列化器
     * @return JSON序列化器实例
     */
    public JsonSerializer getJsonSerializer() {
        return jsonSerializer;
    }
}
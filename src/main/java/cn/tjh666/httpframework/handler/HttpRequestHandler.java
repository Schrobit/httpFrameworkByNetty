package cn.tjh666.httpframework.handler;

import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;
import cn.tjh666.httpframework.exception.ExceptionMapper;
import cn.tjh666.httpframework.json.JsonSerializer;
import cn.tjh666.httpframework.routing.Route;
import cn.tjh666.httpframework.routing.Router;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.Method;

/**
 * HTTP请求处理器
 * 负责处理所有HTTP请求，包括路由匹配、方法调用和异常处理
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Router router;
    private final JsonSerializer jsonSerializer;
    private final ExceptionMapper exceptionMapper;

    /**
     * 构造请求处理器
     * @param router 路由器
     * @param jsonSerializer JSON序列化器
     * @param exceptionMapper 异常处理器
     */
    public HttpRequestHandler(Router router, JsonSerializer jsonSerializer, ExceptionMapper exceptionMapper) {
        this.router = router;
        this.jsonSerializer = jsonSerializer;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        Request request = new Request(httpRequest);
        Response response = new Response(ctx, jsonSerializer);
        
        try {
            // 查找匹配的路由
            Route route = router.findRoute(request);
            
            if (route == null) {
                // 404 Not Found
                response.sendError(404, "Not Found: " + request.getUri());
                return;
            }
            
            // 调用控制器方法
            invokeControllerMethod(route, request, response);
            
        } catch (Exception e) {
            // 使用异常处理器处理异常
            try {
                exceptionMapper.handle(e, response);
            } catch (Exception ex) {
                // 如果异常处理器也出错，发送基本错误响应
                if (!response.isSent()) {
                    response.sendError(500, "Internal Server Error");
                }
            }
        }
    }

    /**
     * 调用控制器方法
     * @param route 路由信息
     * @param request 请求对象
     * @param response 响应对象
     * @throws Exception 调用异常
     */
    private void invokeControllerMethod(Route route, Request request, Response response) throws Exception {
        Method method = route.getMethod();
        Object controller = route.getController();
        
        // 设置方法可访问
        method.setAccessible(true);
        
        // 准备方法参数
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] == Request.class) {
                args[i] = request;
            } else if (paramTypes[i] == Response.class) {
                args[i] = response;
            } else {
                // 其他参数类型暂不支持，设为null
                args[i] = null;
            }
        }
        
        // 调用方法
        Object result = method.invoke(controller, args);
        
        // 如果方法有返回值且响应未发送，自动发送JSON响应
        if (result != null && !response.isSent()) {
            response.json(200, result);
        } else if (!response.isSent()) {
            // 如果没有返回值且响应未发送，发送空响应
            response.text(200, "");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        
        // 创建响应对象处理异常
        Response response = new Response(ctx, jsonSerializer);
        try {
            exceptionMapper.handle(new Exception(cause), response);
        } catch (Exception e) {
            if (!response.isSent()) {
                response.sendError(500, "Internal Server Error");
            }
        }
    }
}
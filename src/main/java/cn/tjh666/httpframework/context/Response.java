package cn.tjh666.httpframework.context;

import cn.tjh666.httpframework.json.JsonSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * HTTP响应上下文封装
 * 提供JSON序列化、文本响应等功能
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class Response {
    private final ChannelHandlerContext ctx;
    private final JsonSerializer jsonSerializer;
    private boolean sent = false;

    /**
     * 构造响应对象
     * @param ctx Netty通道上下文
     * @param jsonSerializer JSON序列化器
     */
    public Response(ChannelHandlerContext ctx, JsonSerializer jsonSerializer) {
        this.ctx = ctx;
        this.jsonSerializer = jsonSerializer;
    }

    /**
     * 发送JSON响应
     * @param status HTTP状态码
     * @param object 要序列化的对象
     */
    public void json(int status, Object object) {
        if (sent) {
            throw new IllegalStateException("Response already sent");
        }
        
        try {
            String json = jsonSerializer.serialize(object);
            sendResponse(HttpResponseStatus.valueOf(status), json, "application/json; charset=UTF-8");
        } catch (Exception e) {
            sendError(500, "JSON serialization error: " + e.getMessage());
        }
    }

    /**
     * 发送文本响应
     * @param status HTTP状态码
     * @param text 文本内容
     */
    public void text(int status, String text) {
        if (sent) {
            throw new IllegalStateException("Response already sent");
        }
        
        sendResponse(HttpResponseStatus.valueOf(status), text, "text/plain; charset=UTF-8");
    }

    /**
     * 发送HTML响应
     * @param status HTTP状态码
     * @param html HTML内容
     */
    public void html(int status, String html) {
        if (sent) {
            throw new IllegalStateException("Response already sent");
        }
        
        sendResponse(HttpResponseStatus.valueOf(status), html, "text/html; charset=UTF-8");
    }

    /**
     * 发送错误响应
     * @param status HTTP状态码
     * @param message 错误消息
     */
    public void sendError(int status, String message) {
        if (sent) {
            return;
        }
        
        sendResponse(HttpResponseStatus.valueOf(status), message, "text/plain; charset=UTF-8");
    }

    /**
     * 发送响应的内部方法
     * @param status HTTP状态
     * @param content 响应内容
     * @param contentType 内容类型
     */
    private void sendResponse(HttpResponseStatus status, String content, String contentType) {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
        );
        
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        
        ctx.writeAndFlush(response);
        sent = true;
    }

    /**
     * 检查响应是否已发送
     * @return 是否已发送
     */
    public boolean isSent() {
        return sent;
    }
}
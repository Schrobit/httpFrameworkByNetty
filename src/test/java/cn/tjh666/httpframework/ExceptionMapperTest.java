package cn.tjh666.httpframework;

import cn.tjh666.httpframework.context.Response;
import cn.tjh666.httpframework.exception.DefaultExceptionMapper;
import cn.tjh666.httpframework.exception.ExceptionMapper;
import cn.tjh666.httpframework.json.JsonSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常处理器测试
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
class ExceptionMapperTest {
    
    private ExceptionMapper exceptionMapper;
    private JsonSerializer jsonSerializer;
    private EmbeddedChannel channel;
    
    @BeforeEach
    void setUp() {
        exceptionMapper = new DefaultExceptionMapper();
        jsonSerializer = new JsonSerializer();
        channel = new EmbeddedChannel();
        // 添加一个简单的handler来确保pipeline不为空
        channel.pipeline().addLast("test", new io.netty.channel.ChannelInboundHandlerAdapter());
    }
    
    @Test
    void testCanHandleAllExceptions() {
        assertTrue(exceptionMapper.canHandle(RuntimeException.class));
        assertTrue(exceptionMapper.canHandle(IllegalArgumentException.class));
        assertTrue(exceptionMapper.canHandle(SecurityException.class));
        assertTrue(exceptionMapper.canHandle(Exception.class));
    }
    
    @Test
    void testHandleRuntimeException() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        RuntimeException exception = new RuntimeException("Test runtime exception");
        exceptionMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        // 验证响应已发送到channel
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        exceptionMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
    
    @Test
    void testHandleSecurityException() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        SecurityException exception = new SecurityException("Access denied");
        exceptionMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
    
    @Test
    void testHandleNotFoundLikeException() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        RuntimeException exception = new RuntimeException("Resource not found");
        exceptionMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
    
    @Test
    void testHandleExceptionWithNullMessage() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        RuntimeException exception = new RuntimeException((String) null);
        exceptionMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
    
    @Test
    void testHandleAlreadySentResponse() {
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        // 先发送一个响应
        response.text(200, "Already sent");
        assertTrue(response.isSent());
        
        // 再尝试处理异常
        RuntimeException exception = new RuntimeException("Test exception");
        exceptionMapper.handle(exception, response);
        
        // 响应仍然是已发送状态
        assertTrue(response.isSent());
    }
    
    @Test
    void testCustomExceptionMapper() {
        ExceptionMapper customMapper = new ExceptionMapper() {
            @Override
            public void handle(Exception exception, Response response) {
                response.text(418, "I'm a teapot: " + exception.getMessage());
            }
            
            @Override
            public boolean canHandle(Class<? extends Exception> exceptionClass) {
                return IllegalStateException.class.isAssignableFrom(exceptionClass);
            }
        };
        
        assertTrue(customMapper.canHandle(IllegalStateException.class));
        assertFalse(customMapper.canHandle(RuntimeException.class));
        
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        Response response = new Response(ctx, jsonSerializer);
        
        IllegalStateException exception = new IllegalStateException("Custom error");
        customMapper.handle(exception, response);
        
        assertTrue(response.isSent());
        
        Object responseObj = channel.readOutbound();
        assertNotNull(responseObj);
    }
}
package cn.tjh666.httpframework.exception;

import cn.tjh666.httpframework.context.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认异常处理器
 * 提供统一的错误响应格式
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class DefaultExceptionMapper implements ExceptionMapper {
    
    @Override
    public void handle(Exception exception, Response response) {
        if (response.isSent()) {
            return;
        }
        
        // 创建错误响应对象
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", exception.getMessage() != null ? exception.getMessage() : "Internal Server Error");
        errorResponse.put("type", exception.getClass().getSimpleName());
        
        // 根据异常类型设置HTTP状态码
        int statusCode = getStatusCode(exception);
        
        try {
            response.json(statusCode, errorResponse);
        } catch (Exception e) {
            // 如果JSON序列化失败，发送纯文本错误
            response.sendError(500, "Error processing exception: " + e.getMessage());
        }
    }
    
    @Override
    public boolean canHandle(Class<? extends Exception> exceptionClass) {
        // 默认处理器可以处理所有异常
        return true;
    }
    
    /**
     * 根据异常类型获取HTTP状态码
     * @param exception 异常对象
     * @return HTTP状态码
     */
    private int getStatusCode(Exception exception) {
        // 可以根据具体异常类型返回不同状态码
        if (exception instanceof IllegalArgumentException) {
            return 400; // Bad Request
        }
        if (exception instanceof SecurityException) {
            return 403; // Forbidden
        }
        if (exception instanceof RuntimeException && exception.getMessage() != null && 
            exception.getMessage().toLowerCase().contains("not found")) {
            return 404; // Not Found
        }
        
        return 500; // Internal Server Error
    }
}
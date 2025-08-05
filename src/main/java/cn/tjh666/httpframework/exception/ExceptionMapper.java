package cn.tjh666.httpframework.exception;

import cn.tjh666.httpframework.context.Response;

/**
 * 异常映射器接口
 * 用于统一处理应用程序异常并生成合适的HTTP响应
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public interface ExceptionMapper {
    
    /**
     * 处理异常并生成响应
     * @param exception 异常对象
     * @param response 响应对象
     */
    void handle(Exception exception, Response response);
    
    /**
     * 判断是否可以处理指定类型的异常
     * @param exceptionClass 异常类型
     * @return 是否可以处理
     */
    boolean canHandle(Class<? extends Exception> exceptionClass);
}
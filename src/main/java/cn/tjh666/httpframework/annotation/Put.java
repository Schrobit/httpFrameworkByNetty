package cn.tjh666.httpframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PUT请求注解
 * 用于标记处理PUT请求的方法
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {
    /**
     * 路由路径
     * @return 路径字符串，支持路径参数如 "/users/{id}"
     */
    String value();
}
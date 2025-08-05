package cn.tjh666.httpframework.routing;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 路由信息类
 * 存储路由路径、HTTP方法、处理器方法等信息
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class Route {
    private final String path;
    private final HttpMethod httpMethod;
    private final Object controller;
    private final Method method;
    private final Pattern pathPattern;
    private final String[] pathParamNames;

    /**
     * 构造路由对象
     * @param path 路由路径
     * @param httpMethod HTTP方法
     * @param controller 控制器实例
     * @param method 处理方法
     * @param pathPattern 路径匹配模式
     * @param pathParamNames 路径参数名数组
     */
    public Route(String path, HttpMethod httpMethod, Object controller, Method method, 
                 Pattern pathPattern, String[] pathParamNames) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.controller = controller;
        this.method = method;
        this.pathPattern = pathPattern;
        this.pathParamNames = pathParamNames;
    }

    /**
     * 获取路由路径
     * @return 路径字符串
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取HTTP方法
     * @return HTTP方法
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * 获取控制器实例
     * @return 控制器对象
     */
    public Object getController() {
        return controller;
    }

    /**
     * 获取处理方法
     * @return Method对象
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取路径匹配模式
     * @return Pattern对象
     */
    public Pattern getPathPattern() {
        return pathPattern;
    }

    /**
     * 获取路径参数名数组
     * @return 参数名数组
     */
    public String[] getPathParamNames() {
        return pathParamNames;
    }

    @Override
    public String toString() {
        return httpMethod + " " + path;
    }
}
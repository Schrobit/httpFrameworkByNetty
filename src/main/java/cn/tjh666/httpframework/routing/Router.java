package cn.tjh666.httpframework.routing;

import cn.tjh666.httpframework.annotation.*;
import cn.tjh666.httpframework.context.Request;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路由器
 * 负责路由注册、匹配和参数提取
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class Router {
    private final List<Route> routes = new ArrayList<>();

    /**
     * 注册控制器类
     * @param controllerClass 控制器类
     */
    public void register(Class<?> controllerClass) {
        try {
            Object controller = controllerClass.newInstance();
            Method[] methods = controllerClass.getDeclaredMethods();
            
            for (Method method : methods) {
                registerMethod(controller, method);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to register controller: " + controllerClass.getName(), e);
        }
    }

    /**
     * 注册单个方法
     * @param controller 控制器实例
     * @param method 方法对象
     */
    private void registerMethod(Object controller, Method method) {
        String path = null;
        HttpMethod httpMethod = null;
        
        // 检查HTTP方法注解
        if (method.isAnnotationPresent(Get.class)) {
            path = method.getAnnotation(Get.class).value();
            httpMethod = HttpMethod.GET;
        } else if (method.isAnnotationPresent(Post.class)) {
            path = method.getAnnotation(Post.class).value();
            httpMethod = HttpMethod.POST;
        } else if (method.isAnnotationPresent(Put.class)) {
            path = method.getAnnotation(Put.class).value();
            httpMethod = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(Delete.class)) {
            path = method.getAnnotation(Delete.class).value();
            httpMethod = HttpMethod.DELETE;
        }
        
        if (path != null && httpMethod != null) {
            // 解析路径参数
            PathInfo pathInfo = parsePath(path);
            Route route = new Route(path, httpMethod, controller, method, 
                                  pathInfo.pattern, pathInfo.paramNames);
            routes.add(route);
        }
    }

    /**
     * 查找匹配的路由
     * @param request 请求对象
     * @return 匹配的路由，如果没有找到返回null
     */
    public Route findRoute(Request request) {
        String requestPath = request.getUri();
        HttpMethod requestMethod = request.getMethod();
        
        for (Route route : routes) {
            if (route.getHttpMethod().equals(requestMethod)) {
                Matcher matcher = route.getPathPattern().matcher(requestPath);
                if (matcher.matches()) {
                    // 提取路径参数
                    extractPathParams(request, route, matcher);
                    return route;
                }
            }
        }
        
        return null;
    }

    /**
     * 提取路径参数
     * @param request 请求对象
     * @param route 路由对象
     * @param matcher 匹配器
     */
    private void extractPathParams(Request request, Route route, Matcher matcher) {
        String[] paramNames = route.getPathParamNames();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                String paramValue = matcher.group(i + 1);
                request.setPathParam(paramNames[i], paramValue);
            }
        }
    }

    /**
     * 解析路径，提取参数名和生成正则表达式
     * @param path 路径字符串
     * @return 路径信息
     */
    private PathInfo parsePath(String path) {
        List<String> paramNames = new ArrayList<>();
        String regex = path;
        
        // 查找路径参数 {paramName}
        Pattern paramPattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = paramPattern.matcher(path);
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            paramNames.add(paramName);
            // 将 {paramName} 替换为 ([^/]+)
            regex = regex.replace("{" + paramName + "}", "([^/]+)");
        }
        
        // 确保路径完全匹配
        if (!regex.endsWith("$")) {
            regex += "$";
        }
        if (!regex.startsWith("^")) {
            regex = "^" + regex;
        }
        
        Pattern pattern = Pattern.compile(regex);
        String[] paramArray = paramNames.isEmpty() ? null : paramNames.toArray(new String[0]);
        
        return new PathInfo(pattern, paramArray);
    }

    /**
     * 路径信息内部类
     */
    private static class PathInfo {
        final Pattern pattern;
        final String[] paramNames;
        
        PathInfo(Pattern pattern, String[] paramNames) {
            this.pattern = pattern;
            this.paramNames = paramNames;
        }
    }

    /**
     * 获取所有注册的路由
     * @return 路由列表
     */
    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }
}
package cn.tjh666.httpframework.context;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求上下文封装
 * 提供参数解析、路径参数、查询参数、请求体等功能
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class Request {
    private final FullHttpRequest httpRequest;
    private final String uri;
    private final HttpMethod method;
    private final Map<String, String> pathParams;
    private final Map<String, List<String>> queryParams;
    private String body;

    /**
     * 构造请求对象
     * @param httpRequest Netty HTTP请求对象
     */
    public Request(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.uri = httpRequest.uri();
        this.method = httpRequest.method();
        this.pathParams = new HashMap<>();
        
        // 解析查询参数
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        this.queryParams = decoder.parameters();
        
        // 解析请求体
        if (httpRequest.content().readableBytes() > 0) {
            this.body = httpRequest.content().toString(CharsetUtil.UTF_8);
        }
    }

    /**
     * 获取请求URI
     * @return URI字符串
     */
    public String getUri() {
        return new QueryStringDecoder(uri).path();
    }

    /**
     * 获取HTTP方法
     * @return HTTP方法
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 获取路径参数
     * @param name 参数名
     * @return 参数值
     */
    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    /**
     * 设置路径参数（由路由器调用）
     * @param name 参数名
     * @param value 参数值
     */
    public void setPathParam(String name, String value) {
        pathParams.put(name, value);
    }

    /**
     * 获取查询参数
     * @param name 参数名
     * @return 参数值
     */
    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    /**
     * 获取所有查询参数
     * @param name 参数名
     * @return 参数值列表
     */
    public List<String> getQueryParams(String name) {
        return queryParams.get(name);
    }

    /**
     * 获取请求头
     * @param name 头名称
     * @return 头值
     */
    public String getHeader(String name) {
        return httpRequest.headers().get(name);
    }

    /**
     * 获取请求体
     * @return 请求体字符串
     */
    public String getBody() {
        return body;
    }

    /**
     * 获取原始Netty请求对象
     * @return FullHttpRequest对象
     */
    public FullHttpRequest getHttpRequest() {
        return httpRequest;
    }
}
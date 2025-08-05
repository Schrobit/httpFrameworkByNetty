package cn.tjh666.httpframework.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON序列化器
 * 基于Jackson实现对象与JSON字符串的相互转换
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class JsonSerializer {
    private final ObjectMapper objectMapper;

    /**
     * 构造JSON序列化器
     */
    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 将对象序列化为JSON字符串
     * @param object 要序列化的对象
     * @return JSON字符串
     * @throws Exception 序列化异常
     */
    public String serialize(Object object) throws Exception {
        if (object == null) {
            return "null";
        }
        if (object instanceof String) {
            return (String) object;
        }
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将JSON字符串反序列化为对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 反序列化后的对象
     * @throws Exception 反序列化异常
     */
    public <T> T deserialize(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * 获取ObjectMapper实例
     * @return ObjectMapper对象
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
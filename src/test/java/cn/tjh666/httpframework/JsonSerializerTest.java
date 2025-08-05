package cn.tjh666.httpframework;

import cn.tjh666.httpframework.json.JsonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON序列化器测试
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
class JsonSerializerTest {
    
    private JsonSerializer jsonSerializer;
    
    @BeforeEach
    void setUp() {
        jsonSerializer = new JsonSerializer();
    }
    
    @Test
    void testSerializeString() throws Exception {
        String input = "Hello World";
        String result = jsonSerializer.serialize(input);
        assertEquals("Hello World", result);
    }
    
    @Test
    void testSerializeNull() throws Exception {
        String result = jsonSerializer.serialize(null);
        assertEquals("null", result);
    }
    
    @Test
    void testSerializeMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", 30);
        map.put("active", true);
        
        String result = jsonSerializer.serialize(map);
        
        // 验证JSON包含所有字段
        assertTrue(result.contains("\"name\":\"John\""));
        assertTrue(result.contains("\"age\":30"));
        assertTrue(result.contains("\"active\":true"));
    }
    
    @Test
    void testDeserializeString() throws Exception {
        String json = "\"Hello World\"";
        String result = jsonSerializer.deserialize(json, String.class);
        assertEquals("Hello World", result);
    }
    
    @Test
    void testDeserializeMap() throws Exception {
        String json = "{\"name\":\"John\",\"age\":30}";
        @SuppressWarnings("unchecked")
        Map<String, Object> result = jsonSerializer.deserialize(json, Map.class);
        
        assertEquals("John", result.get("name"));
        assertEquals(30, result.get("age"));
    }
    
    @Test
    void testSerializeComplexObject() throws Exception {
        TestUser user = new TestUser("Alice", 25, "alice@example.com");
        String result = jsonSerializer.serialize(user);
        
        assertTrue(result.contains("\"name\":\"Alice\""));
        assertTrue(result.contains("\"age\":25"));
        assertTrue(result.contains("\"email\":\"alice@example.com\""));
    }
    
    @Test
    void testDeserializeComplexObject() throws Exception {
        String json = "{\"name\":\"Bob\",\"age\":35,\"email\":\"bob@example.com\"}";
        TestUser result = jsonSerializer.deserialize(json, TestUser.class);
        
        assertEquals("Bob", result.getName());
        assertEquals(35, result.getAge());
        assertEquals("bob@example.com", result.getEmail());
    }
    
    /**
     * 测试用户类
     */
    public static class TestUser {
        private String name;
        private int age;
        private String email;
        
        // 默认构造函数（Jackson需要）
        public TestUser() {}
        
        public TestUser(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
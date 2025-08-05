package cn.tjh666.httpframework;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.annotation.Post;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;
import cn.tjh666.httpframework.server.HttpServer;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HTTP服务器集成测试
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
class HttpServerTest {
    
    private HttpServer server;
    private static final int TEST_PORT = 18080;
    
    @BeforeEach
    void setUp() throws Exception {
        server = new HttpServer(TEST_PORT);
        server.register(TestController.class);
        
        // 异步启动服务器
        ChannelFuture future = server.startAsync();
        future.await(5, TimeUnit.SECONDS);
        
        // 等待服务器完全启动
        Thread.sleep(1000);
    }
    
    @AfterEach
    void tearDown() {
        if (server != null) {
            server.shutdown();
        }
    }
    
    @Test
    void testSimpleGetRequest() throws Exception {
        String response = sendGetRequest("/test/hello");
        
        assertTrue(response.contains("\"message\":\"Hello World\""));
        assertTrue(response.contains("\"status\":\"success\""));
    }
    
    @Test
    void testPathParameterRequest() throws Exception {
        String response = sendGetRequest("/test/users/123");
        
        assertTrue(response.contains("\"userId\":\"123\""));
        assertTrue(response.contains("\"message\":\"User found\""));
    }
    
    @Test
    void testQueryParameterRequest() throws Exception {
        String response = sendGetRequest("/test/search?q=java&page=2");
        
        assertTrue(response.contains("\"query\":\"java\""));
        assertTrue(response.contains("\"page\":\"2\""));
    }
    
    @Test
    void testPostRequest() throws Exception {
        String requestBody = "{\"name\":\"John\",\"age\":30}";
        String response = sendPostRequest("/test/users", requestBody);
        
        assertTrue(response.contains("\"message\":\"User created\""));
        assertTrue(response.contains("\"receivedData\""));
    }
    
    @Test
    void testNotFoundRequest() throws Exception {
        try {
            sendGetRequest("/nonexistent");
            fail("Should have thrown exception for 404");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }
    
    @Test
    void testExceptionHandling() throws Exception {
        try {
            sendGetRequest("/test/error");
            fail("Should have thrown exception for 500");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("500"));
        }
    }
    
    @Test
    void testCustomResponse() throws Exception {
        String response = sendGetRequest("/test/custom");
        assertEquals("Custom text response", response);
    }
    
    /**
     * 发送GET请求
     */
    private String sendGetRequest(String path) throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP " + responseCode + ": " + conn.getResponseMessage());
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * 发送POST请求
     */
    private String sendPostRequest(String path, String body) throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        OutputStream os = conn.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP " + responseCode + ": " + conn.getResponseMessage());
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * 测试控制器
     */
    public static class TestController {
        
        @Get("/test/hello")
        public Map<String, Object> hello() {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Hello World");
            result.put("status", "success");
            return result;
        }
        
        @Get("/test/users/{id}")
        public Map<String, Object> getUser(Request request) {
            String userId = request.getPathParam("id");
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("message", "User found");
            return result;
        }
        
        @Get("/test/search")
        public Map<String, Object> search(Request request) {
            String query = request.getQueryParam("q");
            String page = request.getQueryParam("page");
            
            Map<String, Object> result = new HashMap<>();
            result.put("query", query);
            result.put("page", page);
            return result;
        }
        
        @Post("/test/users")
        public Map<String, Object> createUser(Request request) {
            String body = request.getBody();
            Map<String, Object> result = new HashMap<>();
            result.put("message", "User created");
            result.put("receivedData", body);
            return result;
        }
        
        @Get("/test/error")
        public Map<String, Object> error() {
            throw new RuntimeException("Test exception");
        }
        
        @Get("/test/custom")
        public void customResponse(Response response) {
            response.text(200, "Custom text response");
        }
    }
}
package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.*;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户管理API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class UserController {
    
    // 模拟数据库存储
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    static {
        // 初始化一些测试数据
        users.put(1L, new User(1L, "张三", "zhangsan@example.com", 25));
        users.put(2L, new User(2L, "李四", "lisi@example.com", 30));
        users.put(3L, new User(3L, "王五", "wangwu@example.com", 28));
        idGenerator.set(4);
    }
    
    /**
     * 获取所有用户
     * @param request HTTP请求对象
     * @return 用户列表
     */
    @Get("/users")
    public Map<String, Object> getAllUsers(Request request) {
        String pageStr = request.getQueryParam("page");
        String sizeStr = request.getQueryParam("size");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
        
        List<User> userList = new ArrayList<>(users.values());
        int start = (page - 1) * size;
        int end = Math.min(start + size, userList.size());
        
        List<User> pageUsers = start < userList.size() ? 
            userList.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", pageUsers);
        result.put("total", users.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (users.size() + size - 1) / size);
        
        return result;
    }
    
    /**
     * 根据ID获取用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/users/{id}")
    public void getUserById(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User user = users.get(id);
            
            if (user != null) {
                response.json(200, user);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户不存在");
                errorResponse.put("id", id);
                response.json(404, errorResponse);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "无效的用户ID");
            response.json(400, errorResponse);
        }
    }
    
    /**
     * 创建新用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/users")
    public void createUser(Request request, Response response) {
        try {
            String body = request.getBody();
            if (body == null || body.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "请求体不能为空");
                response.json(400, errorResponse);
                return;
            }
            
            // 简单的JSON解析（实际项目中建议使用Jackson）
            Map<String, Object> userData = parseSimpleJson(body);
            
            String name = (String) userData.get("name");
            String email = (String) userData.get("email");
            Integer age = userData.get("age") != null ? 
                Integer.parseInt(userData.get("age").toString()) : null;
            
            if (name == null || email == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "姓名和邮箱是必填项");
                response.json(400, errorResponse);
                return;
            }
            
            Long id = idGenerator.getAndIncrement();
            User newUser = new User(id, name, email, age);
            users.put(id, newUser);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "用户创建成功");
            successResponse.put("user", newUser);
            response.json(201, successResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "创建用户失败: " + e.getMessage());
            response.json(400, errorResponse);
        }
    }
    
    /**
     * 更新用户信息
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Put("/users/{id}")
    public void updateUser(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User existingUser = users.get(id);
            
            if (existingUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户不存在");
                errorResponse.put("id", id);
                response.json(404, errorResponse);
                return;
            }
            
            String body = request.getBody();
            Map<String, Object> updateData = parseSimpleJson(body);
            
            // 更新用户信息
            String name = (String) updateData.get("name");
            String email = (String) updateData.get("email");
            Integer age = updateData.get("age") != null ? 
                Integer.parseInt(updateData.get("age").toString()) : null;
            
            User updatedUser = new User(
                id,
                name != null ? name : existingUser.getName(),
                email != null ? email : existingUser.getEmail(),
                age != null ? age : existingUser.getAge()
            );
            
            users.put(id, updatedUser);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "用户更新成功");
            successResponse.put("user", updatedUser);
            response.json(200, successResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "更新用户失败: " + e.getMessage());
            response.json(400, errorResponse);
        }
    }
    
    /**
     * 删除用户
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Delete("/users/{id}")
    public void deleteUser(Request request, Response response) {
        try {
            Long id = Long.parseLong(request.getPathParam("id"));
            User deletedUser = users.remove(id);
            
            if (deletedUser != null) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "用户删除成功");
                successResponse.put("user", deletedUser);
                response.json(200, successResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户不存在");
                errorResponse.put("id", id);
                response.json(404, errorResponse);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "无效的用户ID");
            response.json(400, errorResponse);
        }
    }
    
    /**
     * 搜索用户
     * @param request HTTP请求对象
     * @return 搜索结果
     */
    @Get("/users/search")
    public Map<String, Object> searchUsers(Request request) {
        String keyword = request.getQueryParam("q");
        
        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("users", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
        
        List<User> matchedUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(keyword.toLowerCase())) {
                matchedUsers.add(user);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", matchedUsers);
        result.put("total", matchedUsers.size());
        result.put("keyword", keyword);
        
        return result;
    }
    
    /**
     * 简单的JSON解析方法（仅用于演示）
     * @param json JSON字符串
     * @return 解析后的Map
     */
    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        // 这里是一个非常简单的JSON解析实现，仅用于演示
        // 实际项目中应该使用Jackson等专业库
        json = json.trim().replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }
        
        return result;
    }
    
    /**
     * 用户实体类
     */
    public static class User {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        
        public User(Long id, String name, String email, Integer age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Integer getAge() { return age; }
        
        // Setters
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setAge(Integer age) { this.age = age; }
    }
}
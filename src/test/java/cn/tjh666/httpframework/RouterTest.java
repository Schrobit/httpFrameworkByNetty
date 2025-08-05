package cn.tjh666.httpframework;

import cn.tjh666.httpframework.annotation.*;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.routing.Route;
import cn.tjh666.httpframework.routing.Router;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 路由器测试
 * 
 * @author Schrobit
 * @email admin@tjh666.cn
 */
class RouterTest {
    
    private Router router;
    
    @BeforeEach
    void setUp() {
        router = new Router();
        router.register(TestController.class);
    }
    
    @Test
    void testSimpleGetRoute() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/hello", route.getPath());
        assertEquals(HttpMethod.GET, route.getHttpMethod());
        assertEquals("hello", route.getMethod().getName());
    }
    
    @Test
    void testPathParameterRoute() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, "/users/123");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/users/{id}", route.getPath());
        assertEquals("123", request.getPathParam("id"));
    }
    
    @Test
    void testMultiplePathParameters() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, "/users/123/posts/456");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/users/{userId}/posts/{postId}", route.getPath());
        assertEquals("123", request.getPathParam("userId"));
        assertEquals("456", request.getPathParam("postId"));
    }
    
    @Test
    void testPostRoute() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.POST, "/users",
            Unpooled.copiedBuffer("{\"name\":\"John\"}", CharsetUtil.UTF_8));
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/users", route.getPath());
        assertEquals(HttpMethod.POST, route.getHttpMethod());
        assertEquals("createUser", route.getMethod().getName());
    }
    
    @Test
    void testPutRoute() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.PUT, "/users/123");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/users/{id}", route.getPath());
        assertEquals(HttpMethod.PUT, route.getHttpMethod());
        assertEquals("updateUser", route.getMethod().getName());
    }
    
    @Test
    void testDeleteRoute() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.DELETE, "/users/123");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("/users/{id}", route.getPath());
        assertEquals(HttpMethod.DELETE, route.getHttpMethod());
        assertEquals("deleteUser", route.getMethod().getName());
    }
    
    @Test
    void testRouteNotFound() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, "/nonexistent");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNull(route);
    }
    
    @Test
    void testMethodNotAllowed() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.PATCH, "/hello");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNull(route);
    }
    
    @Test
    void testQueryParameters() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, "/search?q=test&page=2");
        Request request = new Request(httpRequest);
        
        Route route = router.findRoute(request);
        
        assertNotNull(route);
        assertEquals("test", request.getQueryParam("q"));
        assertEquals("2", request.getQueryParam("page"));
    }
    
    /**
     * 测试控制器类
     */
    public static class TestController {
        
        @Get("/hello")
        public String hello() {
            return "Hello World";
        }
        
        @Get("/users/{id}")
        public String getUser() {
            return "Get User";
        }
        
        @Get("/users/{userId}/posts/{postId}")
        public String getUserPost() {
            return "Get User Post";
        }
        
        @Post("/users")
        public String createUser() {
            return "Create User";
        }
        
        @Put("/users/{id}")
        public String updateUser() {
            return "Update User";
        }
        
        @Delete("/users/{id}")
        public String deleteUser() {
            return "Delete User";
        }
        
        @Get("/search")
        public String search() {
            return "Search";
        }
    }
}
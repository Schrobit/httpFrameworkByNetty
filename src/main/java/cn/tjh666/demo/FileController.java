package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.annotation.Post;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件服务API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class FileController {
    
    private static final String UPLOAD_DIR = "uploads";
    
    static {
        // 确保上传目录存在
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件列表
     * @param request HTTP请求对象
     * @return 文件列表
     */
    @Get("/files")
    public Map<String, Object> listFiles(Request request) {
        try {
            List<Map<String, Object>> files = new ArrayList<>();
            
            Files.list(Paths.get(UPLOAD_DIR))
                .forEach(path -> {
                    try {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("name", path.getFileName().toString());
                        fileInfo.put("size", Files.size(path));
                        fileInfo.put("lastModified", Files.getLastModifiedTime(path).toString());
                        fileInfo.put("isDirectory", Files.isDirectory(path));
                        files.add(fileInfo);
                    } catch (IOException e) {
                        // 忽略单个文件的错误
                    }
                });
            
            Map<String, Object> result = new HashMap<>();
            result.put("files", files);
            result.put("total", files.size());
            result.put("uploadDir", UPLOAD_DIR);
            
            return result;
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "读取文件列表失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * 获取文件内容
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/files/{filename}")
    public void getFile(Request request, Response response) {
        String filename = request.getPathParam("filename");
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        
        try {
            if (!Files.exists(filePath)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "文件不存在: " + filename);
                response.json(404, errorResponse);
                return;
            }
            
            if (Files.isDirectory(filePath)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "不能下载目录: " + filename);
                response.json(400, errorResponse);
                return;
            }
            
            // 读取文件内容
            String content = new String(Files.readAllBytes(filePath));
            
            Map<String, Object> fileData = new HashMap<>();
            fileData.put("filename", filename);
            fileData.put("content", content);
            fileData.put("size", Files.size(filePath));
            fileData.put("lastModified", Files.getLastModifiedTime(filePath).toString());
            
            response.json(200, fileData);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "读取文件失败: " + e.getMessage());
            response.json(500, errorResponse);
        }
    }
    
    /**
     * 上传文件（简单文本文件）
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/files/upload")
    public void uploadFile(Request request, Response response) {
        try {
            String filename = request.getQueryParam("filename");
            String content = request.getBody();
            
            if (filename == null || filename.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "文件名不能为空");
                response.json(400, errorResponse);
                return;
            }
            
            if (content == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "文件内容不能为空");
                response.json(400, errorResponse);
                return;
            }
            
            // 生成唯一文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueFilename = timestamp + "_" + filename;
            
            Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);
            Files.write(filePath, content.getBytes());
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "文件上传成功");
            result.put("filename", uniqueFilename);
            result.put("originalName", filename);
            result.put("size", content.length());
            result.put("uploadTime", LocalDateTime.now().toString());
            
            response.json(201, result);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "文件上传失败: " + e.getMessage());
            response.json(500, errorResponse);
        }
    }
}
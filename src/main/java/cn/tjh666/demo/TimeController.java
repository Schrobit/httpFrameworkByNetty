package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间服务示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class TimeController {
    
    /**
     * 获取当前时间（JSON格式）
     * @param request HTTP请求对象
     * @return 时间信息
     */
    @Get("/time")
    public Map<String, Object> getCurrentTime(Request request) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> timeInfo = new HashMap<>();
        
        timeInfo.put("timestamp", System.currentTimeMillis());
        timeInfo.put("datetime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        timeInfo.put("date", now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        timeInfo.put("time", now.format(DateTimeFormatter.ISO_LOCAL_TIME));
        timeInfo.put("year", now.getYear());
        timeInfo.put("month", now.getMonthValue());
        timeInfo.put("day", now.getDayOfMonth());
        timeInfo.put("hour", now.getHour());
        timeInfo.put("minute", now.getMinute());
        timeInfo.put("second", now.getSecond());
        
        return timeInfo;
    }
    
    /**
     * 获取格式化时间
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Get("/time/formatted")
    public void getFormattedTime(Request request, Response response) {
        String format = request.getQueryParam("format");
        LocalDateTime now = LocalDateTime.now();
        
        String formattedTime;
        if (format != null && !format.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                formattedTime = now.format(formatter);
            } catch (Exception e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "无效的时间格式: " + format);
                response.json(400, errorResponse);
                return;
            }
        } else {
            formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("formatted_time", formattedTime);
        response.json(200, result);
    }
    
    /**
     * 获取时区时间
     * @param request HTTP请求对象
     * @return 时区时间信息
     */
    @Get("/time/timezone/{zone}")
    public Map<String, Object> getTimeByZone(Request request) {
        String zone = request.getPathParam("zone");
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里简化处理，实际应用中可以使用ZoneId
            LocalDateTime now = LocalDateTime.now();
            result.put("zone", zone);
            result.put("local_time", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            result.put("note", "时区功能需要进一步实现");
        } catch (Exception e) {
            result.put("error", "无效的时区: " + zone);
        }
        
        return result;
    }
}
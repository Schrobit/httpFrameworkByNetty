package cn.tjh666.demo;

import cn.tjh666.httpframework.annotation.Get;
import cn.tjh666.httpframework.annotation.Post;
import cn.tjh666.httpframework.context.Request;
import cn.tjh666.httpframework.context.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算器API示例
 * @author Schrobit
 * @email admin@tjh666.cn
 */
public class CalculatorController {
    
    /**
     * 基本算术运算
     * @param request HTTP请求对象
     * @return 计算结果
     */
    @Get("/calc/{operation}")
    public Map<String, Object> calculate(Request request) {
        String operation = request.getPathParam("operation");
        String aStr = request.getQueryParam("a");
        String bStr = request.getQueryParam("b");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (aStr == null || bStr == null) {
                result.put("error", "参数a和b是必需的");
                return result;
            }
            
            double a = Double.parseDouble(aStr);
            double b = Double.parseDouble(bStr);
            double calcResult;
            
            switch (operation.toLowerCase()) {
                case "add":
                    calcResult = a + b;
                    break;
                case "subtract":
                    calcResult = a - b;
                    break;
                case "multiply":
                    calcResult = a * b;
                    break;
                case "divide":
                    if (b == 0) {
                        result.put("error", "除数不能为零");
                        return result;
                    }
                    calcResult = a / b;
                    break;
                case "power":
                    calcResult = Math.pow(a, b);
                    break;
                default:
                    result.put("error", "不支持的运算: " + operation);
                    result.put("supported", new String[]{"add", "subtract", "multiply", "divide", "power"});
                    return result;
            }
            
            result.put("operation", operation);
            result.put("a", a);
            result.put("b", b);
            result.put("result", calcResult);
            result.put("expression", a + " " + getOperationSymbol(operation) + " " + b + " = " + calcResult);
            
        } catch (NumberFormatException e) {
            result.put("error", "无效的数字格式");
        }
        
        return result;
    }
    
    /**
     * 复杂表达式计算
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @Post("/calc/expression")
    public void evaluateExpression(Request request, Response response) {
        try {
            String body = request.getBody();
            Map<String, Object> requestData = parseSimpleJson(body);
            String expression = (String) requestData.get("expression");
            
            if (expression == null || expression.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "表达式不能为空");
                response.json(400, errorResponse);
                return;
            }
            
            // 简单的表达式计算（仅支持基本运算）
            double result = evaluateSimpleExpression(expression);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("expression", expression);
            responseData.put("result", result);
            responseData.put("note", "仅支持简单的四则运算");
            
            response.json(200, responseData);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "表达式计算失败: " + e.getMessage());
            response.json(400, errorResponse);
        }
    }
    
    /**
     * 数学函数计算
     * @param request HTTP请求对象
     * @return 计算结果
     */
    @Get("/calc/math/{function}")
    public Map<String, Object> mathFunction(Request request) {
        String function = request.getPathParam("function");
        String xStr = request.getQueryParam("x");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (xStr == null) {
                result.put("error", "参数x是必需的");
                return result;
            }
            
            double x = Double.parseDouble(xStr);
            double calcResult;
            
            switch (function.toLowerCase()) {
                case "sin":
                    calcResult = Math.sin(x);
                    break;
                case "cos":
                    calcResult = Math.cos(x);
                    break;
                case "tan":
                    calcResult = Math.tan(x);
                    break;
                case "sqrt":
                    if (x < 0) {
                        result.put("error", "负数不能开平方根");
                        return result;
                    }
                    calcResult = Math.sqrt(x);
                    break;
                case "log":
                    if (x <= 0) {
                        result.put("error", "对数的真数必须大于0");
                        return result;
                    }
                    calcResult = Math.log(x);
                    break;
                case "log10":
                    if (x <= 0) {
                        result.put("error", "对数的真数必须大于0");
                        return result;
                    }
                    calcResult = Math.log10(x);
                    break;
                case "abs":
                    calcResult = Math.abs(x);
                    break;
                case "ceil":
                    calcResult = Math.ceil(x);
                    break;
                case "floor":
                    calcResult = Math.floor(x);
                    break;
                default:
                    result.put("error", "不支持的数学函数: " + function);
                    result.put("supported", new String[]{"sin", "cos", "tan", "sqrt", "log", "log10", "abs", "ceil", "floor"});
                    return result;
            }
            
            result.put("function", function);
            result.put("x", x);
            result.put("result", calcResult);
            result.put("expression", function + "(" + x + ") = " + calcResult);
            
        } catch (NumberFormatException e) {
            result.put("error", "无效的数字格式");
        }
        
        return result;
    }
    
    /**
     * 获取运算符号
     * @param operation 运算名称
     * @return 运算符号
     */
    private String getOperationSymbol(String operation) {
        switch (operation.toLowerCase()) {
            case "add": return "+";
            case "subtract": return "-";
            case "multiply": return "*";
            case "divide": return "/";
            case "power": return "^";
            default: return operation;
        }
    }
    
    /**
     * 简单表达式计算（仅用于演示）
     * @param expression 表达式
     * @return 计算结果
     */
    private double evaluateSimpleExpression(String expression) {
        // 这是一个非常简单的实现，仅支持基本的四则运算
        // 实际项目中应该使用专业的表达式解析库
        expression = expression.replaceAll("\\s+", "");
        
        // 简单处理加法
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
        }
        
        // 简单处理减法
        if (expression.contains("-") && !expression.startsWith("-")) {
            String[] parts = expression.split("-");
            return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
        }
        
        // 简单处理乘法
        if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
        }
        
        // 简单处理除法
        if (expression.contains("/")) {
            String[] parts = expression.split("/");
            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
        }
        
        // 如果没有运算符，直接返回数字
        return Double.parseDouble(expression);
    }
    
    /**
     * 简单的JSON解析方法
     * @param json JSON字符串
     * @return 解析后的Map
     */
    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
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
}
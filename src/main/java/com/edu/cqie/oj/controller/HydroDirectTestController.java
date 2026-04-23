package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.config.HydroJudgeConfig;
import com.edu.cqie.oj.domain.Result;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Hydro 直接测试控制器（绕过 Service）
 */
@Slf4j
@RestController
@RequestMapping("/api/test/hydro-direct")
public class HydroDirectTestController {

    @Autowired
    private HydroJudgeConfig hydroJudgeConfig;

    @GetMapping("/login-and-submit")
    public Result<Map<String, Object>> testLoginAndSubmit() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 创建 HTTP 客户端（禁用重定向）
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(30000, TimeUnit.MILLISECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build();

            // 2. 登录
            String username = hydroJudgeConfig.getUsername();
            String password = hydroJudgeConfig.getPassword();
            
            log.info("开始登录 Hydro: username={}", username);
            
            RequestBody loginBody = new FormBody.Builder()
                    .add("uname", username)
                    .add("password", password)
                    .add("login_submit", "")
                    .build();

            String loginUrl = hydroJudgeConfig.getBaseUrl() + "/login";
            log.info("登录 URL: {}", loginUrl);
            log.info("用户名: {}", username);
            
            Request loginRequest = new Request.Builder()
                    .url(loginUrl)
                    .post(loginBody)
                    .build();

            log.info("发送登录请求...");
            String sessionCookie = null;
            try (Response loginResponse = client.newCall(loginRequest).execute()) {
                log.info("登录响应状态码: {}", loginResponse.code());
                log.info("登录响应消息: {}", loginResponse.message());
                log.info("登录响应头: {}", loginResponse.headers());
                
                if (loginResponse.isSuccessful()) {
                    List<String> cookies = loginResponse.headers("Set-Cookie");
                    log.info("Set-Cookie 头数量: {}", cookies.size());
                    
                    if (!cookies.isEmpty()) {
                        sessionCookie = cookies.get(0).split(";")[0];
                        log.info("✓ 登录成功，Cookie: {}", sessionCookie);
                        result.put("loginSuccess", true);
                        result.put("cookie", sessionCookie);
                    } else {
                        log.error("✗ 登录响应中没有 Cookie");
                        result.put("loginSuccess", false);
                        result.put("error", "No cookie in response");
                        return Result.error("登录失败：没有获取到 Cookie");
                    }
                } else {
                    log.error("✗ 登录失败，状态码: {}", loginResponse.code());
                    result.put("loginSuccess", false);
                    result.put("error", "Login failed with code: " + loginResponse.code());
                    return Result.error("登录失败: " + loginResponse.code());
                }
            }

            // 3. 提交代码
            log.info("开始提交代码到题目 1");
            
            String testCode = "#include <stdio.h>\nint main() { return 0; }";
            String submitUrl = hydroJudgeConfig.getBaseUrl() + "/p/1/submit";
            
            RequestBody submitBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lang", "c")
                    .addFormDataPart("code", testCode)
                    .build();

            Request submitRequest = new Request.Builder()
                    .url(submitUrl)
                    .post(submitBody)
                    .addHeader("Cookie", sessionCookie)
                    .build();

            log.info("提交请求 URL: {}", submitUrl);
            
            try (Response submitResponse = client.newCall(submitRequest).execute()) {
                log.info("提交响应状态码: {}", submitResponse.code());
                
                if (submitResponse.isSuccessful()) {
                    String responseBody = submitResponse.body() != null ? submitResponse.body().string() : "";
                    log.info("响应体长度: {}", responseBody.length());
                    
                    // 尝试提取 RID
                    String rid = "unknown";
                    if (responseBody.contains("/record/")) {
                        int startIdx = responseBody.indexOf("/record/") + 8;
                        int endIdx = responseBody.indexOf("\"", startIdx);
                        if (endIdx > startIdx) {
                            rid = responseBody.substring(startIdx, endIdx);
                        }
                    }
                    
                    result.put("submitSuccess", true);
                    result.put("rid", rid);
                    result.put("statusCode", submitResponse.code());
                    
                    log.info("✓ 提交成功，RID: {}", rid);
                    return Result.success(result);
                } else {
                    String errorBody = submitResponse.body() != null ? submitResponse.body().string() : "";
                    log.error("✗ 提交失败，状态码: {}, 响应: {}", submitResponse.code(), errorBody.substring(0, Math.min(200, errorBody.length())));
                    
                    result.put("submitSuccess", false);
                    result.put("statusCode", submitResponse.code());
                    result.put("error", "Submit failed with code: " + submitResponse.code());
                    
                    return Result.error("提交失败: " + submitResponse.code());
                }
            }

        } catch (Exception e) {
            log.error("测试异常", e);
            result.put("error", e.getClass().getName() + ": " + e.getMessage());
            return Result.error("测试异常: " + e.getMessage());
        }
    }
}

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
import java.util.Map;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Hydro 调试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/test/hydro-debug")
public class HydroDebugController {

    @Autowired
    private HydroJudgeConfig hydroJudgeConfig;

    @GetMapping("/test")
    public Result<Map<String, Object>> debugTest() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("baseUrl", hydroJudgeConfig.getBaseUrl());
            result.put("username", hydroJudgeConfig.getUsername());
            result.put("password", hydroJudgeConfig.getPassword() != null ? "***" : "null");
            
            // 测试直接访问 Hydro
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(10000, TimeUnit.MILLISECONDS)
                    .followRedirects(true)
                    .proxy(Proxy.NO_PROXY)  // 禁用代理
                    .build();

            String testUrl = hydroJudgeConfig.getBaseUrl() + "/p/1";
            log.info("测试访问: {}", testUrl);
            
            Request request = new Request.Builder()
                    .url(testUrl)
                    .get()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                result.put("testUrl", testUrl);
                result.put("statusCode", response.code());
                result.put("message", response.message());
                result.put("isSuccessful", response.isSuccessful());
                
                log.info("响应状态码: {}, 消息: {}", response.code(), response.message());
                
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    result.put("errorBody", errorBody.substring(0, Math.min(200, errorBody.length())));
                }
            }

            return Result.success(result);

        } catch (Exception e) {
            log.error("调试测试异常", e);
            result.put("exception", e.getClass().getName());
            result.put("exceptionMessage", e.getMessage());
            return Result.error("调试测试异常: " + e.getMessage());
        }
    }
}

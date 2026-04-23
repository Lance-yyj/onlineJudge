package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.config.HydroJudgeConfig;
import com.edu.cqie.oj.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置测试控制器
 */
@RestController
@RequestMapping("/api/test/config")
public class ConfigTestController {

    @Autowired
    private HydroJudgeConfig hydroJudgeConfig;

    @GetMapping("/hydro")
    public Result<Map<String, Object>> getHydroConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("baseUrl", hydroJudgeConfig.getBaseUrl());
        config.put("username", hydroJudgeConfig.getUsername());
        config.put("password", hydroJudgeConfig.getPassword() != null ? "***" : null);
        config.put("apiToken", hydroJudgeConfig.getApiToken());
        return Result.success(config);
    }
}

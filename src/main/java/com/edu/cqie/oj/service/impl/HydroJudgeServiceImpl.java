package com.edu.cqie.oj.service.impl;

import cn.hutool.json.JSONUtil;
import com.edu.cqie.oj.config.HydroJudgeConfig;
import com.edu.cqie.oj.domain.dto.HydroJudgeResponse;
import com.edu.cqie.oj.domain.dto.HydroStatusResponse;
import com.edu.cqie.oj.domain.dto.HydroSubmitRequest;
import com.edu.cqie.oj.service.IHydroJudgeService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HydroOJ 判题服务实现类
 *
 * @author system
 * @since 1.0
 */
@Slf4j
@Service
public class HydroJudgeServiceImpl implements IHydroJudgeService {

    @Autowired
    private HydroJudgeConfig hydroJudgeConfig;

    private OkHttpClient httpClient;

    // 语言映射表：本地语言 -> HydroOJ语言ID
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("C", "c");
        LANGUAGE_MAP.put("C++", "cc");
        LANGUAGE_MAP.put("CPP", "cpp");
        LANGUAGE_MAP.put("Java", "java");
        LANGUAGE_MAP.put("Python", "python");
        LANGUAGE_MAP.put("Python3", "python3");
        LANGUAGE_MAP.put("JavaScript", "js");
        LANGUAGE_MAP.put("Go", "go");
        LANGUAGE_MAP.put("Rust", "rust");
    }

    public HydroJudgeServiceImpl() {
        // 默认构造函数
    }

    /**
     * 初始化 HTTP 客户端
     */
    private synchronized void initHttpClient() {
        if (httpClient == null) {
            this.httpClient = new OkHttpClient.Builder()
                    .connectTimeout(hydroJudgeConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(hydroJudgeConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(hydroJudgeConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    @Override
    public HydroJudgeResponse submitCode(String pid, String language, String code) {
        log.info("提交代码到 HydroOJ: pid={}, language={}", pid, language);
        
        // 初始化 HTTP 客户端
        initHttpClient();

        try {
            // 构建请求体
            HydroSubmitRequest request = new HydroSubmitRequest();
            request.setPid(pid);
            request.setLang(mapLanguage(language));
            request.setCode(code);

            String jsonBody = JSONUtil.toJsonStr(request);
            log.debug("请求体: {}", jsonBody);

            // 构建 HTTP 请求
            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            String url = hydroJudgeConfig.getBaseUrl() + "/api/submit";
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + hydroJudgeConfig.getApiToken())
                    .build();

            // 发送请求
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("HydroOJ API 调用失败: code={}, message={}", 
                            response.code(), response.message());
                    throw new RuntimeException("HydroOJ API 调用失败: " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.debug("响应体: {}", responseBody);

                // 解析响应
                HydroJudgeResponse judgeResponse = JSONUtil.toBean(responseBody, HydroJudgeResponse.class);
                
                if (judgeResponse.getSuccess() == null || !judgeResponse.getSuccess()) {
                    log.error("HydroOJ 提交失败: {}", judgeResponse.getMessage());
                    throw new RuntimeException("HydroOJ 提交失败: " + judgeResponse.getMessage());
                }

                log.info("代码提交成功，rid={}", judgeResponse.getRid());
                return judgeResponse;
            }

        } catch (IOException e) {
            log.error("调用 HydroOJ API 异常", e);
            throw new RuntimeException("调用 HydroOJ API 异常: " + e.getMessage(), e);
        }
    }

    @Override
    public HydroStatusResponse querySubmissionStatus(String rid) {
        log.info("查询提交状态: rid={}", rid);
        
        // 初始化 HTTP 客户端
        initHttpClient();

        try {
            String url = hydroJudgeConfig.getBaseUrl() + "/api/status/" + rid;
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + hydroJudgeConfig.getApiToken())
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("查询状态失败: code={}", response.code());
                    throw new RuntimeException("查询状态失败: " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.debug("状态响应: {}", responseBody);

                return JSONUtil.toBean(responseBody, HydroStatusResponse.class);
            }

        } catch (IOException e) {
            log.error("查询提交状态异常", e);
            throw new RuntimeException("查询提交状态异常: " + e.getMessage(), e);
        }
    }

    @Override
    public String mapLanguage(String localLanguage) {
        if (localLanguage == null || localLanguage.isEmpty()) {
            return "cpp"; // 默认使用 C++
        }

        String hydroLang = LANGUAGE_MAP.get(localLanguage.toUpperCase());
        if (hydroLang == null) {
            log.warn("未找到语言映射: {}, 使用默认值 cpp", localLanguage);
            return "cpp";
        }

        return hydroLang;
    }

    @Override
    public Integer convertToLocalStatus(Integer hydroStatus) {
        if (hydroStatus == null) {
            return 0; // 等待中
        }

        // HydroOJ 状态码映射:
        // 0: Waiting -> 0: 等待中
        // 1: Accepted -> 2: 通过
        // 2-9: 各种错误 -> 3: 失败
        switch (hydroStatus) {
            case 0:
                return 0; // 等待中
            case 1:
                return 2; // 通过
            default:
                return 3; // 失败
        }
    }
}

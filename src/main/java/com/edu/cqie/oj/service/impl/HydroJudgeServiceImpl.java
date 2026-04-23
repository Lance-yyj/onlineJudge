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
import java.util.List;
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
    private String sessionCookie = null; // 存储登录后的 Cookie

    // 语言映射表：本地语言 -> HydroOJ语言ID
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("C", "c");
        LANGUAGE_MAP.put("C++", "cc");
        LANGUAGE_MAP.put("CPP", "cc");
        LANGUAGE_MAP.put("Java", "java");
        LANGUAGE_MAP.put("Python", "py");
        LANGUAGE_MAP.put("Python3", "py3");
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

    /**
     * 登录 Hydro 获取 Cookie
     */
    private synchronized void loginToHydro() {
        if (sessionCookie != null) {
            return; // 已经登录过
        }

        String username = hydroJudgeConfig.getUsername();
        String password = hydroJudgeConfig.getPassword();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            log.warn("未配置 Hydro 用户名或密码，将尝试无认证访问");
            return;
        }

        log.info("正在登录 Hydro: username={}", username);

        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("uname", username)
                    .add("password", password)
                    .add("login_submit", "")
                    .build();

            String loginUrl = hydroJudgeConfig.getBaseUrl() + "/login";
            Request request = new Request.Builder()
                    .url(loginUrl)
                    .post(formBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // 提取 Cookie
                    List<String> cookies = response.headers("Set-Cookie");
                    if (!cookies.isEmpty()) {
                        sessionCookie = cookies.get(0).split(";")[0];
                        log.info("Hydro 登录成功，Cookie: {}", sessionCookie);
                    } else {
                        log.warn("Hydro 登录响应中没有 Cookie");
                    }
                } else {
                    log.error("Hydro 登录失败: code={}", response.code());
                }
            }
        } catch (IOException e) {
            log.error("Hydro 登录异常", e);
        }
    }

    @Override
    public HydroJudgeResponse submitCode(String pid, String language, String code) {
        log.info("提交代码到 HydroOJ: pid={}, language={}", pid, language);
        
        try {
            // 构建 PowerShell 命令，直接传递代码
            String scriptPath = "D:\\idea\\onlineJudge\\hydro_submit.ps1";
            String username = hydroJudgeConfig.getUsername();
            String password = hydroJudgeConfig.getPassword();
            
            // 对代码进行 Base64 编码，避免特殊字符问题
            String encodedCode = java.util.Base64.getEncoder().encodeToString(code.getBytes("UTF-8"));
            
            String command = String.format(
                "powershell -NoProfile -ExecutionPolicy Bypass -File \"%s\" -username \"%s\" -password \"%s\" -problemId \"%s\" -language \"%s\" -encodedCode \"%s\"",
                scriptPath, username, password, pid, language, encodedCode
            );
            
            log.info("执行 PowerShell 命令（代码已Base64编码）");
            
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream(), "UTF-8")
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("PowerShell 输出: {}", line);
            }
            
            // 等待完成，最多 30 秒
            boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("PowerShell 执行超时（30秒）");
            }
            
            int exitCode = process.exitValue();
            String result = output.toString().trim();
            
            log.info("PowerShell 退出码: {}, 输出: {}", exitCode, result);
            
            if (exitCode == 0 && result.contains("\"success\": true")) {
                HydroJudgeResponse response = new HydroJudgeResponse();
                response.setSuccess(true);
                
                // 提取 RID
                if (result.contains("\"rid\"")) {
                    int ridStart = result.indexOf("\"rid\": \"") + 8;
                    int ridEnd = result.indexOf("\"", ridStart);
                    if (ridEnd > ridStart) {
                        response.setRid(result.substring(ridStart, ridEnd));
                    }
                }
                
                log.info("代码提交成功，rid={}", response.getRid());
                return response;
            } else {
                log.error("Hydro 提交失败，退出码: {}, 输出: {}", exitCode, result);
                throw new RuntimeException("Hydro 提交失败: " + result);
            }
            
        } catch (Exception e) {
            log.error("调用 Hydro 异常", e);
            throw new RuntimeException("调用 Hydro 异常: " + e.getMessage(), e);
        }
    }

    @Override
    public HydroStatusResponse querySubmissionStatus(String rid) {
        log.info("查询提交状态: rid={}", rid);
        
        try {
            // 使用 PowerShell 脚本查询状态
            String scriptPath = "D:\\idea\\onlineJudge\\hydro_query_status.ps1";
            String username = hydroJudgeConfig.getUsername();
            String password = hydroJudgeConfig.getPassword();
            
            String command = String.format(
                "powershell -NoProfile -ExecutionPolicy Bypass -File \"%s\" -username \"%s\" -password \"%s\" -rid \"%s\"",
                scriptPath, username, password, rid
            );
            
            log.info("执行 PowerShell 命令查询状态");
            
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream(), "UTF-8")
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("PowerShell 输出: {}", line);
            }
            
            // 等待完成，最多 30 秒
            boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("PowerShell 执行超时（30秒）");
            }
            
            int exitCode = process.exitValue();
            String result = output.toString().trim();
            
            log.info("PowerShell 退出码: {}, 输出: {}", exitCode, result);
            
            if (exitCode == 0 && result.contains("\"success\": true")) {
                log.info("PowerShell 返回原始数据: {}", result);
                
                // 解析 JSON 响应
                HydroStatusResponse statusResponse = new HydroStatusResponse();
                
                // 提取状态
                if (result.contains("\"status\":")) {
                    int statusStart = result.indexOf("\"status\":") + 9;
                    int statusEnd = result.indexOf(",", statusStart);
                    if (statusEnd == -1) statusEnd = result.indexOf("}", statusStart);
                    if (statusEnd > statusStart) {
                        String statusStr = result.substring(statusStart, statusEnd).trim();
                        log.info("提取到 status 字符串: '{}'", statusStr);
                        statusResponse.setStatus(Integer.parseInt(statusStr));
                    }
                }
                
                // 提取时间
                if (result.contains("\"time\":")) {
                    int timeStart = result.indexOf("\"time\":") + 7;
                    int timeEnd = result.indexOf(",", timeStart);
                    if (timeEnd == -1) timeEnd = result.indexOf("}", timeStart);
                    if (timeEnd > timeStart) {
                        String timeStr = result.substring(timeStart, timeEnd).trim();
                        log.info("提取到 time 字符串: '{}'", timeStr);
                        if (!timeStr.equals("null")) {
                            statusResponse.setTime(Long.parseLong(timeStr));
                        }
                    }
                }
                
                // 提取内存
                if (result.contains("\"memory\":")) {
                    int memStart = result.indexOf("\"memory\":") + 9;
                    int memEnd = result.indexOf(",", memStart);
                    if (memEnd == -1) memEnd = result.indexOf("}", memStart);
                    if (memEnd > memStart) {
                        String memStr = result.substring(memStart, memEnd).trim();
                        log.info("提取到 memory 字符串: '{}'", memStr);
                        if (!memStr.equals("null")) {
                            statusResponse.setMemory(Long.parseLong(memStr));
                        }
                    }
                }
                
                // 设置完成标志
                Integer status = statusResponse.getStatus();
                if (status != null && status >= 1) {
                    statusResponse.setCompleted(true);
                } else {
                    statusResponse.setCompleted(false);
                }
                
                log.info("查询状态成功: status={}, time={}, memory={}, completed={}", 
                    statusResponse.getStatus(), statusResponse.getTime(), statusResponse.getMemory(), statusResponse.getCompleted());
                return statusResponse;
            } else {
                log.error("查询状态失败，退出码: {}, 输出: {}", exitCode, result);
                throw new RuntimeException("查询状态失败: " + result);
            }
            
        } catch (Exception e) {
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

package com.edu.cqie.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HydroOJ 判题系统配置类
 *
 * @author system
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "hydro.judge")
public class HydroJudgeConfig {

    /**
     * HydroOJ API 基础URL
     */
    private String baseUrl = "http://localhost:8888";

    /**
     * API Token
     */
    private String apiToken = "";

    /**
     * Hydro 用户名（用于登录）
     */
    private String username = "";

    /**
     * Hydro 密码（用于登录）
     */
    private String password = "";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 写入超时时间（毫秒）
     */
    private int writeTimeout = 30000;
}

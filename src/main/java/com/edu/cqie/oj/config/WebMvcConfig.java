package com.edu.cqie.oj.config;

import com.edu.cqie.oj.interceptor.PermissionInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 注册权限拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Resource
    private PermissionInterceptor permissionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/user/login",           // 登录接口不需要权限验证
                        "/error",                // 错误页面
                        "/favicon.ico"           // 图标文件
                );
    }
}

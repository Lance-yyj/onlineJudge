package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 用户添加DTO
 */
@Data
public class UserAddDto {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 学号（学生专用）
     */
    private String studentId;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}

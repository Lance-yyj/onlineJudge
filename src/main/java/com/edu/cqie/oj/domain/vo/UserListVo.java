package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户列表VO
 */
@Data
public class UserListVo {
    
    /**
     * 用户ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
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
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}

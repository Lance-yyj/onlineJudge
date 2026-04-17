package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户登录响应VO
 */
@Data
public class UserLoginVo {

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
     * Token（用于后续请求认证）
     */
    private String token;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
}

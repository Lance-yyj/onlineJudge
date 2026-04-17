package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDto {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}

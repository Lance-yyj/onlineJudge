package com.edu.cqie.oj.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.String;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "user")
public class UserEntity {

    /**
     * 用户ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 用户名
     */
    @Column(value = "username")
    private String username;

    /**
     * 密码
     */
    @Column(value = "password")
    private String password;

    /**
     * 邮箱
     */
    @Column(value = "email")
    private String email;

    /**
     * 真实姓名
     */
    @Column(value = "real_name")
    private String realName;

    /**
     * 学号（学生专用）
     */
    @Column(value = "student_id")
    private String studentId;

    /**
     * 头像
     */
    @Column(value = "avatar")
    private String avatar;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(value = "status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @Column(value = "last_login_time")
    private Date lastLoginTime;

    @Column(value = "created_at")
    private Date createdAt;

    @Column(value = "updated_at")
    private Date updatedAt;

}

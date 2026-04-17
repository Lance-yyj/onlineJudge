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
@Table(value = "permission")
public class PermissionEntity {

    /**
     * 权限ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 权限名称
     */
    @Column(value = "permission_name")
    private String permissionName;

    /**
     * 权限描述
     */
    @Column(value = "description")
    private String description;

    /**
     * 权限代码（如user:create）
     */
    @Column(value = "permission_code")
    private String permissionCode;

    /**
     * 所属模块
     */
    @Column(value = "module")
    private String module;

    @Column(value = "created_at")
    private Date createdAt;
}

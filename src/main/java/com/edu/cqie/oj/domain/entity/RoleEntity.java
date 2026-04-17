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
@Table(value = "role")
public class RoleEntity {

    /**
     * 角色ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 角色名称
     */
    @Column(value = "role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @Column(value = "description")
    private String description;

    @Column(value = "created_at")
    private Date createdAt;

    @Column(value = "updated_at")
    private Date updatedAt;
}

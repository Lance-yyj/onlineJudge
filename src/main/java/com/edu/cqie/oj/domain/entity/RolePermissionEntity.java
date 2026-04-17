package com.edu.cqie.oj.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "role_permission")
public class RolePermissionEntity {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 角色ID
     */
    @Column(value = "role_id")
    private Integer roleId;

    /**
     * 权限ID
     */
    @Column(value = "permission_id")
    private Integer permissionId;

    @Column(value = "created_at")
    private Date createdAt;

}

package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 权限列表VO
 */
@Data
public class PermissionListVo {
    
    /**
     * 权限ID
     */
    private Integer id;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 权限代码（如user:create）
     */
    private String permissionCode;
    
    /**
     * 所属模块
     */
    private String module;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}

package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 权限添加DTO
 */
@Data
public class PermissionAddDto {
    
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
}

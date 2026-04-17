package com.edu.cqie.oj.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色分配权限DTO
 */
@Data
public class RolePermissionAssignDto {
    
    /**
     * 角色ID
     */
    private Integer roleId;
    
    /**
     * 权限ID列表
     */
    private List<Integer> permissionIds;
}

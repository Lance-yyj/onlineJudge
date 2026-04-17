package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 角色权限VO
 */
@Data
public class RolePermissionVo {
    
    /**
     * 角色ID
     */
    private Integer roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 权限ID列表
     */
    private List<Integer> permissionIds;
    
    /**
     * 权限名称列表
     */
    private List<String> permissionNames;
}

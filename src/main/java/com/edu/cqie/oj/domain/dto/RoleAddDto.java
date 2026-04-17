package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 角色添加DTO
 */
@Data
public class RoleAddDto {
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色描述
     */
    private String description;
}

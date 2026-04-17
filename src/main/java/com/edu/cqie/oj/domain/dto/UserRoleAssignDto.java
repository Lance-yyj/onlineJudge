package com.edu.cqie.oj.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户分配角色DTO
 */
@Data
public class UserRoleAssignDto {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 角色ID列表
     */
    private List<Integer> roleIds;
}

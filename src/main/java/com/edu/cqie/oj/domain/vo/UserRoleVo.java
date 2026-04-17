package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户角色VO
 */
@Data
public class UserRoleVo {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色ID列表
     */
    private List<Integer> roleIds;
    
    /**
     * 角色名称列表
     */
    private List<String> roleNames;
}

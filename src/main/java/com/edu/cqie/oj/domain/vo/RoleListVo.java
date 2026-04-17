package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 角色列表VO
 */
@Data
public class RoleListVo {
    
    /**
     * 角色ID
     */
    private Integer id;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}

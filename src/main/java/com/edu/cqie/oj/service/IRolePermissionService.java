package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.RolePermissionAssignDto;
import com.edu.cqie.oj.domain.entity.RolePermissionEntity;
import com.edu.cqie.oj.domain.vo.RolePermissionVo;
import com.mybatisflex.core.service.IService;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IRolePermissionService extends IService<RolePermissionEntity> {

    /**
     * 为角色分配权限
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    boolean assignPermissions(RolePermissionAssignDto assignDto);

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return RolePermissionVo
     */
    RolePermissionVo getRolePermissions(Integer roleId);

    /**
     * 移除角色的指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return boolean
     */
    boolean removeRolePermission(Integer roleId, Integer permissionId);
}
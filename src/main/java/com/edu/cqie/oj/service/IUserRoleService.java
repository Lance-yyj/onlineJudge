package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.UserRoleAssignDto;
import com.edu.cqie.oj.domain.entity.UserRoleEntity;
import com.edu.cqie.oj.domain.vo.UserRoleVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IUserRoleService extends IService<UserRoleEntity> {

    /**
     * 为用户分配角色
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    boolean assignRoles(UserRoleAssignDto assignDto);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return UserRoleVo
     */
    UserRoleVo getUserRoles(Integer userId);

    /**
     * 移除用户的指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return boolean
     */
    boolean removeUserRole(Integer userId, Integer roleId);
}
package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.domain.dto.UserRoleAssignDto;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.edu.cqie.oj.domain.entity.RoleEntity;
import com.edu.cqie.oj.domain.entity.RolePermissionEntity;
import com.edu.cqie.oj.domain.entity.UserEntity;
import com.edu.cqie.oj.domain.vo.UserRoleVo;
import com.edu.cqie.oj.mapper.PermissionMapper;
import com.edu.cqie.oj.mapper.RoleMapper;
import com.edu.cqie.oj.mapper.RolePermissionMapper;
import com.edu.cqie.oj.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IUserRoleService;
import com.edu.cqie.oj.domain.entity.UserRoleEntity;
import com.edu.cqie.oj.mapper.UserRoleMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.PermissionEntityTableDef.PERMISSION_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.RolePermissionEntityTableDef.ROLE_PERMISSION_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements IUserRoleService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 为用户分配角色
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(UserRoleAssignDto assignDto) {
        // 验证用户是否存在
        UserEntity user = userMapper.selectOneById(assignDto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 删除该用户原有的所有角色
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", assignDto.getUserId());
        this.remove(queryWrapper);

        // 添加新的角色关联
        if (assignDto.getRoleIds() != null && !assignDto.getRoleIds().isEmpty()) {
            List<UserRoleEntity> userRoleList = new ArrayList<>();
            for (Integer roleId : assignDto.getRoleIds()) {
                // 验证角色是否存在
                RoleEntity role = roleMapper.selectOneById(roleId);
                if (role == null) {
                    throw new RuntimeException("角色ID " + roleId + " 不存在");
                }

                UserRoleEntity userRole = new UserRoleEntity();
                userRole.setUserId(assignDto.getUserId());
                userRole.setRoleId(roleId);
                userRole.setCreatedAt(new Date());
                userRoleList.add(userRole);
            }
            return this.saveBatch(userRoleList);
        }

        return true;
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return UserRoleVo
     */
    @Override
    public UserRoleVo getUserRoles(Integer userId) {
        // 查询用户信息
        UserEntity user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询用户的角色关联
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId);
        List<UserRoleEntity> userRoleList = this.list(queryWrapper);

        UserRoleVo vo = new UserRoleVo();
        vo.setUserId(userId);
        vo.setUsername(user.getUsername());

        if (userRoleList != null && !userRoleList.isEmpty()) {
            List<Integer> roleIds = userRoleList.stream()
                    .map(UserRoleEntity::getRoleId)
                    .collect(Collectors.toList());
            vo.setRoleIds(roleIds);

            // 查询角色名称
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                RoleEntity role = roleMapper.selectOneById(roleId);
                if (role != null) {
                    roleNames.add(role.getRoleName());
                }
            }
            vo.setRoleNames(roleNames);

            // 查询用户的所有权限
            QueryWrapper rolePermissionQuery = QueryWrapper.create()
                    .from(ROLE_PERMISSION_ENTITY)
                    .where(ROLE_PERMISSION_ENTITY.ROLE_ID.in(roleIds));
            List<RolePermissionEntity> rolePermissions = rolePermissionMapper.selectListByQuery(rolePermissionQuery);
            
            if (rolePermissions != null && !rolePermissions.isEmpty()) {
                List<Integer> permissionIds = rolePermissions.stream()
                        .map(RolePermissionEntity::getPermissionId)
                        .distinct()
                        .collect(Collectors.toList());
                
                QueryWrapper permissionQuery = QueryWrapper.create()
                        .from(PERMISSION_ENTITY)
                        .where(PERMISSION_ENTITY.ID.in(permissionIds));
                List<PermissionEntity> permissions = permissionMapper.selectListByQuery(permissionQuery);
                
                List<String> permissionCodes = permissions.stream()
                        .map(PermissionEntity::getPermissionCode)
                        .distinct()
                        .collect(Collectors.toList());
                vo.setPermissions(permissionCodes);
            } else {
                vo.setPermissions(new ArrayList<>());
            }
        } else {
            vo.setRoleIds(new ArrayList<>());
            vo.setRoleNames(new ArrayList<>());
            vo.setPermissions(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 移除用户的指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserRole(Integer userId, Integer roleId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? AND role_id = ?", userId, roleId);
        return this.remove(queryWrapper);
    }
}
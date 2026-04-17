package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.domain.dto.RolePermissionAssignDto;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.edu.cqie.oj.domain.entity.RoleEntity;
import com.edu.cqie.oj.domain.vo.RolePermissionVo;
import com.edu.cqie.oj.mapper.PermissionMapper;
import com.edu.cqie.oj.mapper.RoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IRolePermissionService;
import com.edu.cqie.oj.domain.entity.RolePermissionEntity;
import com.edu.cqie.oj.mapper.RolePermissionMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermissionEntity> implements IRolePermissionService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 为角色分配权限
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(RolePermissionAssignDto assignDto) {
        // 验证角色是否存在
        RoleEntity role = roleMapper.selectOneById(assignDto.getRoleId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 删除该角色原有的所有权限
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("role_id = ?", assignDto.getRoleId());
        this.remove(queryWrapper);

        // 添加新的权限关联
        if (assignDto.getPermissionIds() != null && !assignDto.getPermissionIds().isEmpty()) {
            List<RolePermissionEntity> rolePermissionList = new ArrayList<>();
            for (Integer permissionId : assignDto.getPermissionIds()) {
                // 验证权限是否存在
                PermissionEntity permission = permissionMapper.selectOneById(permissionId);
                if (permission == null) {
                    throw new RuntimeException("权限ID " + permissionId + " 不存在");
                }

                RolePermissionEntity rolePermission = new RolePermissionEntity();
                rolePermission.setRoleId(assignDto.getRoleId());
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreatedAt(new Date());
                rolePermissionList.add(rolePermission);
            }
            return this.saveBatch(rolePermissionList);
        }

        return true;
    }

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return RolePermissionVo
     */
    @Override
    public RolePermissionVo getRolePermissions(Integer roleId) {
        // 查询角色信息
        RoleEntity role = roleMapper.selectOneById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 查询角色的权限关联
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("role_id = ?", roleId);
        List<RolePermissionEntity> rolePermissionList = this.list(queryWrapper);

        RolePermissionVo vo = new RolePermissionVo();
        vo.setRoleId(roleId);
        vo.setRoleName(role.getRoleName());

        if (rolePermissionList != null && !rolePermissionList.isEmpty()) {
            List<Integer> permissionIds = rolePermissionList.stream()
                    .map(RolePermissionEntity::getPermissionId)
                    .collect(Collectors.toList());
            vo.setPermissionIds(permissionIds);

            // 查询权限名称
            List<String> permissionNames = new ArrayList<>();
            for (Integer permissionId : permissionIds) {
                PermissionEntity permission = permissionMapper.selectOneById(permissionId);
                if (permission != null) {
                    permissionNames.add(permission.getPermissionName());
                }
            }
            vo.setPermissionNames(permissionNames);
        } else {
            vo.setPermissionIds(new ArrayList<>());
            vo.setPermissionNames(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 移除角色的指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRolePermission(Integer roleId, Integer permissionId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("role_id = ? AND permission_id = ?", roleId, permissionId);
        return this.remove(queryWrapper);
    }
}
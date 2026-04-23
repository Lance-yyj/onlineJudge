package com.edu.cqie.oj.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.edu.cqie.oj.domain.dto.UserLoginDto;
import com.edu.cqie.oj.domain.vo.UserLoginVo;
import com.edu.cqie.oj.util.JwtUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.edu.cqie.oj.service.IUserService;
import com.edu.cqie.oj.domain.dto.UserAddDto;
import com.edu.cqie.oj.domain.entity.UserEntity;
import com.edu.cqie.oj.domain.vo.UserListVo;
import com.edu.cqie.oj.mapper.UserMapper;
import com.edu.cqie.oj.mapper.UserRoleMapper;
import com.edu.cqie.oj.mapper.RolePermissionMapper;
import com.edu.cqie.oj.mapper.PermissionMapper;
import com.edu.cqie.oj.mapper.RoleMapper;
import com.edu.cqie.oj.domain.entity.UserRoleEntity;
import com.edu.cqie.oj.domain.entity.RolePermissionEntity;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.edu.cqie.oj.domain.entity.RoleEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.RolePermissionEntityTableDef.ROLE_PERMISSION_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.PermissionEntityTableDef.PERMISSION_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public boolean addUser(UserAddDto userAddDto) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userAddDto, userEntity);
        // 使用BCrypt加密密码
        if (StrUtil.isNotBlank(userAddDto.getPassword())) {
            userEntity.setPassword(BCrypt.hashpw(userAddDto.getPassword()));
        }
        userEntity.setCreatedAt(new Date());
        userEntity.setUpdatedAt(new Date());
        return this.save(userEntity);
    }

    @Override
    public List<UserListVo> getUserList() {
        List<UserEntity> userList = this.list();
        return userList.stream().map(user -> {
            UserListVo vo = new UserListVo();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public UserLoginVo login(UserLoginDto loginDto) {
        // 参数校验
        if (StrUtil.isBlank(loginDto.getUsername()) || StrUtil.isBlank(loginDto.getPassword())) {
            throw new RuntimeException("用户名和密码不能为空");
        }

        // 查询用户
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(UserEntity::getUsername).eq(loginDto.getUsername());
        UserEntity user = this.getOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 验证密码（使用BCrypt验证）
        if (!BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        this.updateById(user);

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建返回结果
        UserLoginVo loginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, loginVo);
        loginVo.setToken(token);

        // 获取用户角色和权限
        List<String> roleNames = getUserRoles(user.getId());
        List<String> permissions = getUserPermissions(user.getId());
        loginVo.setRoleNames(roleNames);
        loginVo.setPermissions(permissions);

        return loginVo;
    }

    /**
     * 获取用户的角色名称列表
     *
     * @param userId 用户ID
     * @return 角色名称列表
     */
    private List<String> getUserRoles(Integer userId) {
        QueryWrapper userRoleQuery = QueryWrapper.create()
                .from(USER_ROLE_ENTITY)
                .where(USER_ROLE_ENTITY.USER_ID.eq(userId));
        List<UserRoleEntity> userRoles = userRoleMapper.selectListByQuery(userRoleQuery);

        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有角色ID
        List<Integer> roleIds = userRoles.stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());

        // 查询角色详情
        QueryWrapper roleQuery = QueryWrapper.create()
                .from(com.edu.cqie.oj.domain.entity.table.RoleEntityTableDef.ROLE_ENTITY)
                .where(com.edu.cqie.oj.domain.entity.table.RoleEntityTableDef.ROLE_ENTITY.ID.in(roleIds));
        List<RoleEntity> roles = roleMapper.selectListByQuery(roleQuery);

        // 返回角色名称列表
        return roles.stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的所有权限代码
     *
     * @param userId 用户ID
     * @return 权限代码列表
     */
    private List<String> getUserPermissions(Integer userId) {
        // 查询用户的所有角色
        QueryWrapper userRoleQuery = QueryWrapper.create()
                .from(USER_ROLE_ENTITY)
                .where(USER_ROLE_ENTITY.USER_ID.eq(userId));
        List<UserRoleEntity> userRoles = userRoleMapper.selectListByQuery(userRoleQuery);

        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有角色ID
        List<Integer> roleIds = userRoles.stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());

        // 查询这些角色的所有权限
        QueryWrapper rolePermissionQuery = QueryWrapper.create()
                .from(ROLE_PERMISSION_ENTITY)
                .where(ROLE_PERMISSION_ENTITY.ROLE_ID.in(roleIds));
        List<RolePermissionEntity> rolePermissions = rolePermissionMapper.selectListByQuery(rolePermissionQuery);

        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有权限ID
        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermissionEntity::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 查询权限详情
        QueryWrapper permissionQuery = QueryWrapper.create()
                .from(PERMISSION_ENTITY)
                .where(PERMISSION_ENTITY.ID.in(permissionIds));
        List<PermissionEntity> permissions = permissionMapper.selectListByQuery(permissionQuery);

        // 返回权限代码列表
        return permissions.stream()
                .map(PermissionEntity::getPermissionCode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean logout(Integer userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 查询用户
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 在实际项目中，这里应该清除用户的Token或Session
        // 由于我们使用的是简单的Token机制，这里只需要记录日志即可
        // 如果需要更完善的登出机制，可以考虑使用Redis存储Token

        return true;
    }
}

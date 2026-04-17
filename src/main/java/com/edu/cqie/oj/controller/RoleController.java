package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.RoleAddDto;
import com.edu.cqie.oj.domain.dto.RolePermissionAssignDto;
import com.edu.cqie.oj.domain.vo.RoleListVo;
import com.edu.cqie.oj.domain.vo.RolePermissionVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IRoleService;
import com.edu.cqie.oj.service.IRolePermissionService;

import java.util.List;

/**
 * 角色管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController{

    @Resource
    private IRoleService roleService;

    @Resource
    private IRolePermissionService rolePermissionService;

    /**
     * 新增角色
     *
     * @param roleAddDto 角色添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addRole")
    @RequiresPermission("role:create")
    public Result<Boolean> addRole(@RequestBody RoleAddDto roleAddDto) {
        boolean result = roleService.addRole(roleAddDto);
        return Result.success(result);
    }

    /**
     * 获取角色列表
     *
     * @return Result<List<RoleListVo>>
     */
    @GetMapping("/getRoleList")
    @RequiresPermission("role:list")
    public Result<List<RoleListVo>> getRoleList() {
        List<RoleListVo> list = roleService.getRoleList();
        return Result.success(list);
    }

    /**
     * 为角色分配权限
     *
     * @param assignDto 分配DTO
     * @return Result<Boolean>
     */
    @PostMapping("/assignPermissions")
    @RequiresPermission("role:assign-permission")
    public Result<Boolean> assignPermissions(@RequestBody RolePermissionAssignDto assignDto) {
        boolean result = rolePermissionService.assignPermissions(assignDto);
        return Result.success("分配权限成功", result);
    }

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return Result<RolePermissionVo>
     */
    @GetMapping("/getRolePermissions")
    @RequiresPermission("role:view-permission")
    public Result<RolePermissionVo> getRolePermissions(Integer roleId) {
        RolePermissionVo vo = rolePermissionService.getRolePermissions(roleId);
        return Result.success(vo);
    }

    /**
     * 移除角色的指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return Result<Boolean>
     */
    @PostMapping("/removeRolePermission")
    @RequiresPermission("role:remove-permission")
    public Result<Boolean> removeRolePermission(Integer roleId, Integer permissionId) {
        boolean result = rolePermissionService.removeRolePermission(roleId, permissionId);
        return Result.success("移除权限成功", result);
    }
}
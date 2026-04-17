package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.UserAddDto;
import com.edu.cqie.oj.domain.dto.UserLoginDto;
import com.edu.cqie.oj.domain.dto.UserRoleAssignDto;
import com.edu.cqie.oj.domain.vo.UserListVo;
import com.edu.cqie.oj.domain.vo.UserLoginVo;
import com.edu.cqie.oj.domain.vo.UserRoleVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IUserService;
import com.edu.cqie.oj.service.IUserRoleService;

import java.util.List;

/**
 * 用户管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Resource
    private IUserService userService;

    @Resource
    private IUserRoleService userRoleService;

    /**
     * 新增用户
     *
     * @param userAddDto 用户添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addUser")
    @RequiresPermission("user:create")
    public Result<Boolean> addUser(@RequestBody UserAddDto userAddDto) {
        boolean result = userService.addUser(userAddDto);
        return Result.success(result);
    }

    /**
     * 获取用户列表
     *
     * @return Result<List<UserListVo>>
     */
    @GetMapping("/getUserList")
    @RequiresPermission("user:list")
    public Result<List<UserListVo>> getUserList() {
        List<UserListVo> list = userService.getUserList();
        return Result.success(list);
    }

    /**
     * 用户登录
     *
     * @param loginDto 登录DTO
     * @return Result<UserLoginVo>
     */
    @PostMapping("/login")
    @RequiresPermission(value = {}, requireLogin = false)
    public Result<UserLoginVo> login(@RequestBody UserLoginDto loginDto) {
        UserLoginVo loginVo = userService.login(loginDto);
        return Result.success("登录成功", loginVo);
    }

    /**
     * 用户下线
     *
     * @param userId 用户ID
     * @return Result<Boolean>
     */
    @PostMapping("/logout")
    @RequiresPermission(requireLogin = true)
    public Result<Boolean> logout(Integer userId) {
        boolean result = userService.logout(userId);
        return Result.success("下线成功", result);
    }

    /**
     * 为用户分配角色
     *
     * @param assignDto 分配DTO
     * @return Result<Boolean>
     */
    @PostMapping("/assignRoles")
    @RequiresPermission("user:assign-role")
    public Result<Boolean> assignRoles(@RequestBody UserRoleAssignDto assignDto) {
        boolean result = userRoleService.assignRoles(assignDto);
        return Result.success("分配角色成功", result);
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return Result<UserRoleVo>
     */
    @GetMapping("/getUserRoles")
    @RequiresPermission("user:view-role")
    public Result<UserRoleVo> getUserRoles(Integer userId) {
        UserRoleVo vo = userRoleService.getUserRoles(userId);
        return Result.success(vo);
    }

    /**
     * 移除用户的指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return Result<Boolean>
     */
    @PostMapping("/removeUserRole")
    @RequiresPermission("user:remove-role")
    public Result<Boolean> removeUserRole(Integer userId, Integer roleId) {
        boolean result = userRoleService.removeUserRole(userId, roleId);
        return Result.success("移除角色成功", result);
    }
}
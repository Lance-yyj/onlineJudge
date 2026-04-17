package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.PermissionAddDto;
import com.edu.cqie.oj.domain.vo.PermissionListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IPermissionService;

import java.util.List;

/**
 * 权限管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/permission")
public class PermissionController extends BaseController{

    @Resource
    private IPermissionService permissionService;

    /**
     * 新增权限
     *
     * @param permissionAddDto 权限添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addPermission")
    @RequiresPermission("permission:create")
    public Result<Boolean> addPermission(@RequestBody PermissionAddDto permissionAddDto) {
        boolean result = permissionService.addPermission(permissionAddDto);
        return Result.success(result);
    }

    /**
     * 获取权限列表
     *
     * @return Result<List<PermissionListVo>>
     */
    @GetMapping("/getPermissionList")
    @RequiresPermission("permission:list")
    public Result<List<PermissionListVo>> getPermissionList() {
        List<PermissionListVo> list = permissionService.getPermissionList();
        return Result.success(list);
    }
}
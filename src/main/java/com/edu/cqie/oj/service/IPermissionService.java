package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.PermissionAddDto;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.edu.cqie.oj.domain.vo.PermissionListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IPermissionService extends IService<PermissionEntity> {

    /**
     * 新增权限
     *
     * @param permissionAddDto 权限添加DTO
     * @return 是否成功
     */
    boolean addPermission(PermissionAddDto permissionAddDto);

    /**
     * 获取权限列表
     *
     * @return 权限列表VO
     */
    List<PermissionListVo> getPermissionList();
}
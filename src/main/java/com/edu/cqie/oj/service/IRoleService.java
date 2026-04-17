package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.RoleAddDto;
import com.edu.cqie.oj.domain.entity.RoleEntity;
import com.edu.cqie.oj.domain.vo.RoleListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IRoleService extends IService<RoleEntity> {

    /**
     * 新增角色
     *
     * @param roleAddDto 角色添加DTO
     * @return 是否成功
     */
    boolean addRole(RoleAddDto roleAddDto);

    /**
     * 获取角色列表
     *
     * @return 角色列表VO
     */
    List<RoleListVo> getRoleList();
}
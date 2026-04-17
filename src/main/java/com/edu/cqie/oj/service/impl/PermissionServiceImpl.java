package com.edu.cqie.oj.service.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.edu.cqie.oj.service.IPermissionService;
import com.edu.cqie.oj.domain.dto.PermissionAddDto;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.edu.cqie.oj.domain.vo.PermissionListVo;
import com.edu.cqie.oj.mapper.PermissionMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

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
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements IPermissionService {

    @Override
    public boolean addPermission(PermissionAddDto permissionAddDto) {
        PermissionEntity permissionEntity = new PermissionEntity();
        BeanUtils.copyProperties(permissionAddDto, permissionEntity);
        permissionEntity.setCreatedAt(new Date());
        return this.save(permissionEntity);
    }

    @Override
    public List<PermissionListVo> getPermissionList() {
        List<PermissionEntity> permissionList = this.list();
        return permissionList.stream().map(permission -> {
            PermissionListVo vo = new PermissionListVo();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}

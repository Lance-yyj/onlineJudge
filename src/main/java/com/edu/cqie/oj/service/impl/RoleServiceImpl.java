package com.edu.cqie.oj.service.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.edu.cqie.oj.service.IRoleService;
import com.edu.cqie.oj.domain.dto.RoleAddDto;
import com.edu.cqie.oj.domain.entity.RoleEntity;
import com.edu.cqie.oj.domain.vo.RoleListVo;
import com.edu.cqie.oj.mapper.RoleMapper;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements IRoleService {

    @Override
    public boolean addRole(RoleAddDto roleAddDto) {
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleAddDto, roleEntity);
        roleEntity.setCreatedAt(new Date());
        roleEntity.setUpdatedAt(new Date());
        return this.save(roleEntity);
    }

    @Override
    public List<RoleListVo> getRoleList() {
        List<RoleEntity> roleList = this.list();
        return roleList.stream().map(role -> {
            RoleListVo vo = new RoleListVo();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}

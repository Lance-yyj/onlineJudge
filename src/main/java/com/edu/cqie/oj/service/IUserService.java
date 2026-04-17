package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.UserAddDto;
import com.edu.cqie.oj.domain.dto.UserLoginDto;
import com.edu.cqie.oj.domain.entity.UserEntity;
import com.edu.cqie.oj.domain.vo.UserListVo;
import com.edu.cqie.oj.domain.vo.UserLoginVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 新增用户
     *
     * @param userAddDto 用户添加DTO
     * @return 是否成功
     */
    boolean addUser(UserAddDto userAddDto);

    /**
     * 获取用户列表
     *
     * @return 用户列表VO
     */
    List<UserListVo> getUserList();

    /**
     * 用户登录
     *
     * @param loginDto 登录DTO
     * @return 登录响应VO
     */
    UserLoginVo login(UserLoginDto loginDto);

    /**
     * 用户下线
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean logout(Integer userId);
}
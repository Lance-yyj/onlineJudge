package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.dto.AnnouncementAddDto;
import com.edu.cqie.oj.domain.vo.AnnouncementListVo;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.edu.cqie.oj.service.IAnnouncementService;
import com.edu.cqie.oj.domain.entity.AnnouncementEntity;
import com.edu.cqie.oj.mapper.AnnouncementMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.AnnouncementEntityTableDef.ANNOUNCEMENT_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity> implements IAnnouncementService {

    /**
     * 新增公告
     *
     * @param announcementAddDto 公告添加DTO
     * @return boolean
     */
    @Override
    public boolean addAnnouncement(AnnouncementAddDto announcementAddDto) {
        AnnouncementEntity announcement = new AnnouncementEntity();
        BeanUtils.copyProperties(announcementAddDto, announcement);
        
        // 从UserContext获取当前用户ID作为发布者
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未登录，请先登录");
        }
        announcement.setPublisherId(currentUserId);
        
        announcement.setCreatedAt(new Date());
        announcement.setUpdatedAt(new Date());
        return save(announcement);
    }

    /**
     * 获取公告列表
     *
     * @return List<AnnouncementListVo>
     */
    @Override
    public List<AnnouncementListVo> getAnnouncementList() {
        // 获取当前用户
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        
        QueryWrapper queryWrapper = QueryWrapper.create();
        
        // 如果不是管理员，只显示可见的公告
        if (currentUser == null || !currentUser.hasPermission("announcement:update")) {
            queryWrapper.where(ANNOUNCEMENT_ENTITY.VISIBLE.eq(1));
        }
        
        List<AnnouncementEntity> list = list(queryWrapper);
        return list.stream().map(entity -> {
            AnnouncementListVo vo = new AnnouncementListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return AnnouncementListVo
     */
    @Override
    public AnnouncementListVo getAnnouncementById(Integer id) {
        AnnouncementEntity entity = getById(id);
        if (entity != null) {
            AnnouncementListVo vo = new AnnouncementListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     * @return boolean
     */
    @Override
    public boolean updateAnnouncement(AnnouncementEntity announcement) {
        announcement.setUpdatedAt(new Date());
        return updateById(announcement);
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return boolean
     */
    @Override
    public boolean deleteAnnouncement(Integer id) {
        return removeById(id);
    }
}
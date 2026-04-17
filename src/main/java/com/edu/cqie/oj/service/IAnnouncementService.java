package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.AnnouncementAddDto;
import com.edu.cqie.oj.domain.entity.AnnouncementEntity;
import com.edu.cqie.oj.domain.vo.AnnouncementListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IAnnouncementService extends IService<AnnouncementEntity> {

    /**
     * 新增公告
     *
     * @param announcementAddDto 公告添加DTO
     * @return boolean
     */
    boolean addAnnouncement(AnnouncementAddDto announcementAddDto);

    /**
     * 获取公告列表
     *
     * @return List<AnnouncementListVo>
     */
    List<AnnouncementListVo> getAnnouncementList();

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return AnnouncementListVo
     */
    AnnouncementListVo getAnnouncementById(Integer id);

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     * @return boolean
     */
    boolean updateAnnouncement(AnnouncementEntity announcement);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return boolean
     */
    boolean deleteAnnouncement(Integer id);
}
package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.AnnouncementAddDto;
import com.edu.cqie.oj.domain.entity.AnnouncementEntity;
import com.edu.cqie.oj.domain.vo.AnnouncementListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IAnnouncementService;

import java.util.List;

/**
 * 公告管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController extends BaseController{

    @Resource
    private IAnnouncementService announcementService;

    /**
     * 新增公告
     *
     * @param announcementAddDto 公告添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addAnnouncement")
    @RequiresPermission("announcement:create")
    public Result<Boolean> addAnnouncement(@RequestBody AnnouncementAddDto announcementAddDto) {
        boolean result = announcementService.addAnnouncement(announcementAddDto);
        return Result.success(result);
    }

    /**
     * 获取公告列表
     *
     * @return Result<List<AnnouncementListVo>>
     */
    @GetMapping("/getAnnouncementList")
    @RequiresPermission("announcement:list")
    public Result<List<AnnouncementListVo>> getAnnouncementList() {
        List<AnnouncementListVo> list = announcementService.getAnnouncementList();
        return Result.success(list);
    }

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return Result<AnnouncementListVo>
     */
    @GetMapping("/getAnnouncementById")
    @RequiresPermission("announcement:view")
    public Result<AnnouncementListVo> getAnnouncementById(Integer id) {
        AnnouncementListVo vo = announcementService.getAnnouncementById(id);
        return Result.success(vo);
    }

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     * @return Result<Boolean>
     */
    @PostMapping("/updateAnnouncement")
    @RequiresPermission("announcement:update")
    public Result<Boolean> updateAnnouncement(@RequestBody AnnouncementEntity announcement) {
        boolean result = announcementService.updateAnnouncement(announcement);
        return Result.success(result);
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return Result<Boolean>
     */
    @PostMapping("/deleteAnnouncement")
    @RequiresPermission("announcement:delete")
    public Result<Boolean> deleteAnnouncement(Integer id) {
        boolean result = announcementService.deleteAnnouncement(id);
        return Result.success(result);
    }
}
package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 公告列表VO
 */
@Data
public class AnnouncementListVo {

    /**
     * 公告ID
     */
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布者ID
     */
    private Integer publisherId;

    /**
     * 重要程度：1-一般，2-重要，3-紧急
     */
    private Integer importance;

    /**
     * 是否可见
     */
    private Integer visible;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

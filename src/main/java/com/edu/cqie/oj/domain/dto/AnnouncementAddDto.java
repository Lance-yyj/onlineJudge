package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 公告添加DTO
 */
@Data
public class AnnouncementAddDto {

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
}

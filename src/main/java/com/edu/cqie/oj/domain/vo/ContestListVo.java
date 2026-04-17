package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 比赛列表VO
 */
@Data
public class ContestListVo {

    /**
     * 比赛ID
     */
    private Integer id;

    /**
     * 比赛标题
     */
    private String title;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 比赛类型：1-公开，2-私有
     */
    private Integer contestType;

    /**
     * 创建者ID
     */
    private Integer creatorId;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 状态：0-未开始，1-进行中，2-已结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

package com.edu.cqie.oj.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 比赛添加DTO
 */
@Data
public class ContestAddDto {

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
     * 访问密码（私有比赛）
     */
    private String password;

    /**
     * 创建者ID
     */
    private Integer creatorId;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;
}

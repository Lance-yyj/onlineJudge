package com.edu.cqie.oj.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.String;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "contest")
public class ContestEntity {

    /**
     * 比赛ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 比赛标题
     */
    @Column(value = "title")
    private String title;

    /**
     * 比赛描述
     */
    @Column(value = "description")
    private String description;

    /**
     * 开始时间
     */
    @Column(value = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(value = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 比赛类型：1-公开，2-私有
     */
    @Column(value = "contest_type")
    private Integer contestType;

    /**
     * 访问密码（私有比赛）
     */
    @Column(value = "password")
    private String password;

    /**
     * 创建者ID
     */
    @Column(value = "creator_id")
    private Integer creatorId;

    /**
     * 最大参与人数
     */
    @Column(value = "max_participants")
    private Integer maxParticipants;

    /**
     * 状态：0-未开始，1-进行中，2-已结束
     */
    @Column(value = "status")
    private Integer status;

    @Column(value = "created_at")
    private Date createdAt;

    @Column(value = "updated_at")
    private Date updatedAt;
}

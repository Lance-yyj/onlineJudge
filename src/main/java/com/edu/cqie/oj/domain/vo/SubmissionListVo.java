package com.edu.cqie.oj.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 提交记录列表VO
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
public class SubmissionListVo {

    /**
     * 提交ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 题目ID
     */
    private Integer problemId;

    /**
     * 比赛ID（可选）
     */
    private Integer contestId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 状态：0-等待中，1-判题中，2-通过，3-失败
     */
    private Integer status;

    /**
     * 执行时间（ms）
     */
    private Integer executeTime;

    /**
     * 内存使用（KB）
     */
    private Integer executeMemory;

    /**
     * 判题结果详情
     */
    private Object judgeResult;

    /**
     * 外部判题机提交ID
     */
    private String externalSubmissionId;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submittedAt;
}

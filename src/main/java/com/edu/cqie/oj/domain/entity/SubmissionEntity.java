package com.edu.cqie.oj.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.Object;
import java.lang.String;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "submission")
public class SubmissionEntity {

    /**
     * 提交ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 用户ID
     */
    @Column(value = "user_id")
    private Integer userId;

    /**
     * 题目ID
     */
    @Column(value = "problem_id")
    private Integer problemId;

    /**
     * 比赛ID（可选）
     */
    @Column(value = "contest_id")
    private Integer contestId;

    /**
     * 提交代码
     */
    @Column(value = "code")
    private String code;

    /**
     * 编程语言
     */
    @Column(value = "language")
    private String language;

    /**
     * 状态：0-等待中，1-判题中，2-通过，3-失败
     */
    @Column(value = "status")
    private Integer status;

    /**
     * 执行时间（ms）
     */
    @Column(value = "execute_time")
    private Integer executeTime;

    /**
     * 内存使用（KB）
     */
    @Column(value = "execute_memory")
    private Integer executeMemory;

    /**
     * 判题结果详情
     */
    @Column(value = "judge_result")
    private Object judgeResult;

    /**
     * 外部判题机提交ID
     */
    @Column(value = "external_submission_id")
    private String externalSubmissionId;

    @Column(value = "submitted_at")
    private Date submittedAt;

}

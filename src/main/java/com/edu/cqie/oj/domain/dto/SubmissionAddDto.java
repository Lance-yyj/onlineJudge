package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * 提交记录添加DTO
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
public class SubmissionAddDto {

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
     * 提交代码
     */
    private String code;

    /**
     * 编程语言
     */
    private String language;
}

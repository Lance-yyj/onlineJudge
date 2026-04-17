package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * HydroOJ 代码提交请求DTO
 *
 * @author system
 * @since 1.0
 */
@Data
public class HydroSubmitRequest {

    /**
     * 题目ID（HydroOJ中的pid）
     */
    private String pid;

    /**
     * 语言ID（如：cc, cpp, java, python3等）
     */
    private String lang;

    /**
     * 源代码
     */
    private String code;

    /**
     * 输入数据（可选，用于自定义测试）
     */
    private String input;

    /**
     * 比赛ID（可选）
     */
    private String contestId;
}

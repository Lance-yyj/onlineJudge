package com.edu.cqie.oj.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 题目列表VO
 */
@Data
public class ProblemListVo {

    /**
     * 题目ID
     */
    private Integer id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目描述
     */
    private String description;

    /**
     * 难度：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 时间限制（ms）
     */
    private Integer timeLimit;

    /**
     * 内存限制（MB）
     */
    private Integer memoryLimit;

    /**
     * 输入描述
     */
    private String inputDesc;

    /**
     * 输出描述
     */
    private String outputDesc;

    /**
     * 样例输入
     */
    private String sampleInput;

    /**
     * 样例输出
     */
    private String sampleOutput;

    /**
     * 测试用例集
     */
    private Object testCases;

    /**
     * 题目标签
     */
    private String tags;

    /**
     * 创建者ID
     */
    private Integer creatorId;

    /**
     * 是否可见
     */
    private Integer visible;

    /**
     * 提交次数
     */
    private Integer submissionCount;

    /**
     * 通过次数
     */
    private Integer acceptedCount;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}

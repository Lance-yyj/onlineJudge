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
@Table(value = "problem")
public class ProblemEntity {

    /**
     * 题目ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 题目标题
     */
    @Column(value = "title")
    private String title;

    /**
     * 题目描述
     */
    @Column(value = "description")
    private String description;

    /**
     * 难度：1-简单，2-中等，3-困难
     */
    @Column(value = "difficulty")
    private Integer difficulty;

    /**
     * 时间限制（ms）
     */
    @Column(value = "time_limit")
    private Integer timeLimit;

    /**
     * 内存限制（MB）
     */
    @Column(value = "memory_limit")
    private Integer memoryLimit;

    /**
     * 输入描述
     */
    @Column(value = "input_desc")
    private String inputDesc;

    /**
     * 输出描述
     */
    @Column(value = "output_desc")
    private String outputDesc;

    /**
     * 样例输入
     */
    @Column(value = "sample_input")
    private String sampleInput;

    /**
     * 样例输出
     */
    @Column(value = "sample_output")
    private String sampleOutput;

    /**
     * 测试用例集
     */
    @Column(value = "test_cases")
    private Object testCases;

    /**
     * 题目标签
     */
    @Column(value = "tags")
    private String tags;

    /**
     * 创建者ID
     */
    @Column(value = "creator_id")
    private Integer creatorId;

    /**
     * 是否可见
     */
    @Column(value = "visible")
    private Integer visible;

    /**
     * 提交次数
     */
    @Column(value = "submission_count")
    private Integer submissionCount;

    /**
     * 通过次数
     */
    @Column(value = "accepted_count")
    private Integer acceptedCount;

    @Column(value = "created_at")
    private Date createdAt;

    @Column(value = "updated_at")
    private Date updatedAt;
}

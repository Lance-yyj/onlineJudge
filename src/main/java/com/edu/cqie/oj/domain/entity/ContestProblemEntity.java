package com.edu.cqie.oj.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "contest_problem")
public class ContestProblemEntity {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 比赛ID
     */
    @Column(value = "contest_id")
    private Integer contestId;

    /**
     * 题目ID
     */
    @Column(value = "problem_id")
    private Integer problemId;

    /**
     * 题目在比赛中的顺序
     */
    @Column(value = "problem_order")
    private Integer problemOrder;

    /**
     * 题目分值
     */
    @Column(value = "score")
    private Integer score;

    @Column(value = "created_at")
    private Date createdAt;
}

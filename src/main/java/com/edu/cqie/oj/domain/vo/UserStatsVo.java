package com.edu.cqie.oj.domain.vo;

import lombok.Data;

/**
 * 用户统计数据VO
 *
 * @author system
 * @since 1.0
 */
@Data
public class UserStatsVo {

    /**
     * 提交总数
     */
    private Long totalSubmissions;

    /**
     * 通过题目数
     */
    private Long acceptedProblems;

    /**
     * 通过率
     */
    private String passRate;

    /**
     * 参与比赛数
     */
    private Long contestCount;
}

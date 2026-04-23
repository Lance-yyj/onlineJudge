package com.edu.cqie.oj.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 比赛题目VO
 */
@Data
public class ContestProblemVo {
    
    /**
     * 比赛ID
     */
    private Integer contestId;
    
    /**
     * 比赛标题
     */
    private String contestTitle;
    
    /**
     * 题目列表
     */
    private List<ProblemDetail> problems;
    
    /**
     * 题目详情
     */
    @Data
    public static class ProblemDetail {
        /**
         * 关联ID
         */
        private Integer id;
        
        /**
         * 题目ID
         */
        private Integer problemId;
        
        /**
         * 题目标题
         */
        private String problemTitle;
        
        /**
         * 题目难度
         */
        private Integer difficulty;
        
        /**
         * 题目在比赛中的顺序
         */
        private Integer problemOrder;
        
        /**
         * 题目分值
         */
        private Integer score;
        
        /**
         * 创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createdAt;
    }
}

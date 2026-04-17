package com.edu.cqie.oj.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 比赛分配题目DTO
 */
@Data
public class ContestProblemAssignDto {
    
    /**
     * 比赛ID
     */
    private Integer contestId;
    
    /**
     * 题目ID列表
     */
    private List<ProblemInfo> problems;
    
    /**
     * 题目信息（包含题目ID、顺序和分值）
     */
    @Data
    public static class ProblemInfo {
        /**
         * 题目ID
         */
        private Integer problemId;
        
        /**
         * 题目在比赛中的顺序
         */
        private Integer problemOrder;
        
        /**
         * 题目分值
         */
        private Integer score;
    }
}

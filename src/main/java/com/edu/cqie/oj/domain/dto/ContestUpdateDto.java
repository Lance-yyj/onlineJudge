package com.edu.cqie.oj.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 比赛更新DTO
 */
@Data
public class ContestUpdateDto {

    /**
     * 比赛ID
     */
    private Integer id;

    /**
     * 比赛标题
     */
    private String title;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 比赛类型：1-公开，2-私有
     */
    private Integer contestType;

    /**
     * 访问密码（私有比赛）
     */
    private String password;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;
    
    /**
     * 状态：0-未开始，1-进行中，2-已结束
     */
    private Integer status;
    
    /**
     * 比赛题目列表（可选）
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

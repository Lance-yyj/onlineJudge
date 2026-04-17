package com.edu.cqie.oj.domain.dto;

import lombok.Data;

/**
 * HydroOJ 判题结果响应DTO
 *
 * @author system
 * @since 1.0
 */
@Data
public class HydroJudgeResponse {

    /**
     * 提交ID
     */
    private String rid;

    /**
     * 状态码
     * 0: Waiting, 1: Accepted, 2: Wrong Answer, 3: Time Limit Exceeded,
     * 4: Memory Limit Exceeded, 5: Output Limit Exceeded, 6: Runtime Error,
     * 7: Compile Error, 8: System Error, 9: Cancelled
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 执行时间（毫秒）
     */
    private Long time;

    /**
     * 内存使用（KB）
     */
    private Long memory;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 编译信息
     */
    private String compileMessage;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 测试点详情
     */
    private Object testCases;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误消息
     */
    private String message;
}

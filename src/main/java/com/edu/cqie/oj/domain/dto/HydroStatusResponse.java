package com.edu.cqie.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * HydroOJ 提交状态查询响应DTO
 *
 * @author system
 * @since 1.0
 */
@Data
public class HydroStatusResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 提交ID
     */
    private String rid;

    /**
     * 状态码
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
     * 是否已完成判题
     */
    private Boolean completed;
}

package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.SubmissionAddDto;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.domain.vo.SubmissionListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface ISubmissionService extends IService<SubmissionEntity> {

    /**
     * 新增提交记录
     *
     * @param submissionAddDto 提交记录添加DTO
     * @return boolean
     */
    boolean addSubmission(SubmissionAddDto submissionAddDto);

    /**
     * 获取提交记录列表
     *
     * @return List<SubmissionListVo>
     */
    List<SubmissionListVo> getSubmissionList();

    /**
     * 根据ID获取提交记录详情
     *
     * @param id 提交记录ID
     * @return SubmissionListVo
     */
    SubmissionListVo getSubmissionById(Integer id);

    /**
     * 根据用户ID获取提交记录列表
     *
     * @param userId 用户ID
     * @return List<SubmissionListVo>
     */
    List<SubmissionListVo> getSubmissionByUserId(Integer userId);

    /**
     * 根据题目ID获取提交记录列表
     *
     * @param problemId 题目ID
     * @return List<SubmissionListVo>
     */
    List<SubmissionListVo> getSubmissionByProblemId(Integer problemId);

    /**
     * 更新提交记录状态
     *
     * @param id 提交记录ID
     * @param status 状态
     * @param executeTime 执行时间
     * @param executeMemory 内存使用
     * @param judgeResult 判题结果
     * @return boolean
     */
    boolean updateSubmissionStatus(Integer id, Integer status, Integer executeTime, Integer executeMemory, Object judgeResult);

    /**
     * 删除提交记录
     *
     * @param id 提交记录ID
     * @return boolean
     */
    boolean deleteSubmission(Integer id);
}
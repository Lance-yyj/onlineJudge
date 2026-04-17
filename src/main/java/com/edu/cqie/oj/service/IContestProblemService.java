package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.ContestProblemAssignDto;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.vo.ContestProblemVo;
import com.mybatisflex.core.service.IService;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IContestProblemService extends IService<ContestProblemEntity> {

    /**
     * 为比赛分配题目
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    boolean assignProblems(ContestProblemAssignDto assignDto);

    /**
     * 获取比赛的题目列表
     *
     * @param contestId 比赛ID
     * @return ContestProblemVo
     */
    ContestProblemVo getContestProblems(Integer contestId);

    /**
     * 移除比赛的指定题目
     *
     * @param contestId 比赛ID
     * @param problemId 题目ID
     * @return boolean
     */
    boolean removeContestProblem(Integer contestId, Integer problemId);
}
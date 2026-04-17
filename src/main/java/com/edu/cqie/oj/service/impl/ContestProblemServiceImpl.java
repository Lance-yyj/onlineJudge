package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.domain.dto.ContestProblemAssignDto;
import com.edu.cqie.oj.domain.entity.ContestEntity;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.domain.vo.ContestProblemVo;
import com.edu.cqie.oj.mapper.ContestMapper;
import com.edu.cqie.oj.mapper.ProblemMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IContestProblemService;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class ContestProblemServiceImpl extends ServiceImpl<ContestProblemMapper, ContestProblemEntity> implements IContestProblemService {

    @Resource
    private ContestMapper contestMapper;

    @Resource
    private ProblemMapper problemMapper;

    /**
     * 为比赛分配题目
     *
     * @param assignDto 分配DTO
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignProblems(ContestProblemAssignDto assignDto) {
        // 验证比赛是否存在
        ContestEntity contest = contestMapper.selectOneById(assignDto.getContestId());
        if (contest == null) {
            throw new RuntimeException("比赛不存在");
        }

        // 删除该比赛原有的所有题目
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("contest_id = ?", assignDto.getContestId());
        this.remove(queryWrapper);

        // 添加新的题目关联
        if (assignDto.getProblems() != null && !assignDto.getProblems().isEmpty()) {
            List<ContestProblemEntity> contestProblemList = new ArrayList<>();
            for (ContestProblemAssignDto.ProblemInfo problemInfo : assignDto.getProblems()) {
                // 验证题目是否存在
                ProblemEntity problem = problemMapper.selectOneById(problemInfo.getProblemId());
                if (problem == null) {
                    throw new RuntimeException("题目ID " + problemInfo.getProblemId() + " 不存在");
                }

                ContestProblemEntity contestProblem = new ContestProblemEntity();
                contestProblem.setContestId(assignDto.getContestId());
                contestProblem.setProblemId(problemInfo.getProblemId());
                contestProblem.setProblemOrder(problemInfo.getProblemOrder());
                contestProblem.setScore(problemInfo.getScore());
                contestProblem.setCreatedAt(new Date());
                contestProblemList.add(contestProblem);
            }
            return this.saveBatch(contestProblemList);
        }

        return true;
    }

    /**
     * 获取比赛的题目列表
     *
     * @param contestId 比赛ID
     * @return ContestProblemVo
     */
    @Override
    public ContestProblemVo getContestProblems(Integer contestId) {
        // 查询比赛信息
        ContestEntity contest = contestMapper.selectOneById(contestId);
        if (contest == null) {
            throw new RuntimeException("比赛不存在");
        }

        // 查询比赛的题目关联
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("contest_id = ?", contestId)
                .orderBy("problem_order ASC");
        List<ContestProblemEntity> contestProblemList = this.list(queryWrapper);

        ContestProblemVo vo = new ContestProblemVo();
        vo.setContestId(contestId);
        vo.setContestTitle(contest.getTitle());

        if (contestProblemList != null && !contestProblemList.isEmpty()) {
            List<ContestProblemVo.ProblemDetail> problemDetails = new ArrayList<>();
            for (ContestProblemEntity contestProblem : contestProblemList) {
                // 查询题目信息
                ProblemEntity problem = problemMapper.selectOneById(contestProblem.getProblemId());
                if (problem != null) {
                    ContestProblemVo.ProblemDetail detail = new ContestProblemVo.ProblemDetail();
                    detail.setId(contestProblem.getId());
                    detail.setProblemId(problem.getId());
                    detail.setProblemTitle(problem.getTitle());
                    detail.setDifficulty(problem.getDifficulty());
                    detail.setProblemOrder(contestProblem.getProblemOrder());
                    detail.setScore(contestProblem.getScore());
                    detail.setCreatedAt(contestProblem.getCreatedAt());
                    problemDetails.add(detail);
                }
            }
            vo.setProblems(problemDetails);
        } else {
            vo.setProblems(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 移除比赛的指定题目
     *
     * @param contestId 比赛ID
     * @param problemId 题目ID
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeContestProblem(Integer contestId, Integer problemId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("contest_id = ? AND problem_id = ?", contestId, problemId);
        return this.remove(queryWrapper);
    }
}
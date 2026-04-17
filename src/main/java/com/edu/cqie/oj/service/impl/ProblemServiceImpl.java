package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.dto.ProblemAddDto;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.domain.vo.ProblemListVo;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.edu.cqie.oj.mapper.SubmissionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IProblemService;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.mapper.ProblemMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.ProblemEntityTableDef.PROBLEM_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.SubmissionEntityTableDef.SUBMISSION_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, ProblemEntity> implements IProblemService {

    @Resource
    private SubmissionMapper submissionMapper;
    
    @Resource
    private ContestProblemMapper contestProblemMapper;

    /**
     * 新增题目
     *
     * @param problemAddDto 题目添加DTO
     * @return boolean
     */
    @Override
    public boolean addProblem(ProblemAddDto problemAddDto) {
        ProblemEntity problem = new ProblemEntity();
        BeanUtils.copyProperties(problemAddDto, problem);
        
        // 从UserContext获取当前用户ID作为创建者
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未登录，请先登录");
        }
        problem.setCreatorId(currentUserId);
        
        problem.setCreatedAt(new Date());
        problem.setUpdatedAt(new Date());
        // 初始化提交次数和通过次数
        if (problem.getSubmissionCount() == null) {
            problem.setSubmissionCount(0);
        }
        if (problem.getAcceptedCount() == null) {
            problem.setAcceptedCount(0);
        }
        return save(problem);
    }

    /**
     * 获取题目列表
     *
     * @return List<ProblemListVo>
     */
    @Override
    public List<ProblemListVo> getProblemList() {
        // 获取当前用户
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        
        QueryWrapper queryWrapper = QueryWrapper.create();
        
        // 如果不是管理员或教师，只显示可见的题目
        if (currentUser == null || !currentUser.hasPermission("problem:update")) {
            queryWrapper.where(PROBLEM_ENTITY.VISIBLE.eq(1));
        }
        
        List<ProblemEntity> list = list(queryWrapper);
        return list.stream().map(entity -> {
            ProblemListVo vo = new ProblemListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取题目详情
     *
     * @param id 题目ID
     * @return ProblemListVo
     */
    @Override
    public ProblemListVo getProblemById(Integer id) {
        ProblemEntity entity = getById(id);
        if (entity != null) {
            ProblemListVo vo = new ProblemListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }

    /**
     * 更新题目
     *
     * @param problem 题目实体
     * @return boolean
     */
    @Override
    public boolean updateProblem(ProblemEntity problem) {
        problem.setUpdatedAt(new Date());
        return updateById(problem);
    }

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProblem(Integer id) {
        // 检查是否有提交记录
        QueryWrapper submissionQuery = QueryWrapper.create()
            .where(SUBMISSION_ENTITY.PROBLEM_ID.eq(id));
        long submissionCount = submissionMapper.selectCountByQuery(submissionQuery);
        if (submissionCount > 0) {
            throw new RuntimeException("该题目已有" + submissionCount + "条提交记录，无法删除");
        }
        
        // 删除比赛题目关联
        QueryWrapper contestProblemQuery = QueryWrapper.create()
            .where("problem_id = ?", id);
        contestProblemMapper.deleteByQuery(contestProblemQuery);
        
        return removeById(id);
    }
}
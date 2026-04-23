package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.dto.ContestAddDto;
import com.edu.cqie.oj.domain.dto.ContestProblemAssignDto;
import com.edu.cqie.oj.domain.dto.ContestUpdateDto;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.domain.vo.ContestListVo;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.edu.cqie.oj.mapper.SubmissionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IContestProblemService;
import com.edu.cqie.oj.service.IContestService;
import com.edu.cqie.oj.domain.entity.ContestEntity;
import com.edu.cqie.oj.mapper.ContestMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.SubmissionEntityTableDef.SUBMISSION_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class ContestServiceImpl extends ServiceImpl<ContestMapper, ContestEntity> implements IContestService {

    @Resource
    private ContestProblemMapper contestProblemMapper;
    
    @Resource
    private SubmissionMapper submissionMapper;
    
    @Resource
    private IContestProblemService contestProblemService;

    /**
     * 新增比赛
     *
     * @param contestAddDto 比赛添加DTO
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addContest(ContestAddDto contestAddDto) {
        // 验证时间逻辑
        if (contestAddDto.getEndTime().before(contestAddDto.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        if (contestAddDto.getStartTime().before(new Date())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        
        ContestEntity contest = new ContestEntity();
        BeanUtils.copyProperties(contestAddDto, contest);
        // 设置创建者ID为当前登录用户
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        contest.setCreatorId(currentUserId);
        // 设置初始状态为未开始
        contest.setStatus(0);
        contest.setCreatedAt(new Date());
        contest.setUpdatedAt(new Date());
        
        // 保存比赛
        boolean result = save(contest);
        
        // 如果有题目列表，自动分配题目
        if (result && contestAddDto.getProblems() != null && !contestAddDto.getProblems().isEmpty()) {
            ContestProblemAssignDto assignDto = new ContestProblemAssignDto();
            assignDto.setContestId(contest.getId());
            
            // 转换题目信息
            List<ContestProblemAssignDto.ProblemInfo> problemInfos = contestAddDto.getProblems().stream()
                .map(dto -> {
                    ContestProblemAssignDto.ProblemInfo info = new ContestProblemAssignDto.ProblemInfo();
                    info.setProblemId(dto.getProblemId());
                    info.setProblemOrder(dto.getProblemOrder());
                    info.setScore(dto.getScore());
                    return info;
                })
                .collect(Collectors.toList());
            
            assignDto.setProblems(problemInfos);
            
            // 分配题目
            contestProblemService.assignProblems(assignDto);
        }
        
        return result;
    }

    /**
     * 获取比赛列表
     *
     * @return List<ContestListVo>
     */
    @Override
    public List<ContestListVo> getContestList() {
        List<ContestEntity> list = list();
        Date now = new Date();
        
        return list.stream().map(entity -> {
            // 动态计算比赛状态
            if (now.before(entity.getStartTime())) {
                entity.setStatus(0); // 未开始
            } else if (now.after(entity.getEndTime())) {
                entity.setStatus(2); // 已结束
            } else {
                entity.setStatus(1); // 进行中
            }
            
            ContestListVo vo = new ContestListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取比赛详情
     *
     * @param id 比赛ID
     * @return ContestListVo
     */
    @Override
    public ContestListVo getContestById(Integer id) {
        ContestEntity entity = getById(id);
        if (entity != null) {
            // 动态计算比赛状态
            Date now = new Date();
            if (now.before(entity.getStartTime())) {
                entity.setStatus(0); // 未开始
            } else if (now.after(entity.getEndTime())) {
                entity.setStatus(2); // 已结束
            } else {
                entity.setStatus(1); // 进行中
            }
            
            ContestListVo vo = new ContestListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }

    /**
     * 更新比赛
     *
     * @param contestUpdateDto 比赛更新DTO
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContest(ContestUpdateDto contestUpdateDto) {
        // 验证时间逻辑
        if (contestUpdateDto.getEndTime().before(contestUpdateDto.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        ContestEntity contest = new ContestEntity();
        BeanUtils.copyProperties(contestUpdateDto, contest);
        contest.setUpdatedAt(new Date());
        
        // 更新比赛基本信息
        boolean result = updateById(contest);
        
        // 如果有题目列表，更新题目关联
        if (result && contestUpdateDto.getProblems() != null) {
            ContestProblemAssignDto assignDto = new ContestProblemAssignDto();
            assignDto.setContestId(contest.getId());
            
            // 转换题目信息
            List<ContestProblemAssignDto.ProblemInfo> problemInfos = contestUpdateDto.getProblems().stream()
                .map(dto -> {
                    ContestProblemAssignDto.ProblemInfo info = new ContestProblemAssignDto.ProblemInfo();
                    info.setProblemId(dto.getProblemId());
                    info.setProblemOrder(dto.getProblemOrder());
                    info.setScore(dto.getScore());
                    return info;
                })
                .collect(Collectors.toList());
            
            assignDto.setProblems(problemInfos);
            
            // 分配题目（会先删除旧的再添加新的）
            contestProblemService.assignProblems(assignDto);
        }
        
        return result;
    }

    /**
     * 删除比赛
     *
     * @param id 比赛ID
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContest(Integer id) {
        // 检查是否有提交记录
        QueryWrapper submissionQuery = QueryWrapper.create()
            .where(SUBMISSION_ENTITY.CONTEST_ID.eq(id));
        long submissionCount = submissionMapper.selectCountByQuery(submissionQuery);
        if (submissionCount > 0) {
            throw new RuntimeException("该比赛已有" + submissionCount + "条提交记录，无法删除");
        }
        
        // 删除比赛题目关联
        QueryWrapper contestProblemQuery = QueryWrapper.create()
            .where("contest_id = ?", id);
        contestProblemMapper.deleteByQuery(contestProblemQuery);
        
        return removeById(id);
    }
}
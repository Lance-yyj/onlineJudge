package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.dto.HydroJudgeResponse;
import com.edu.cqie.oj.domain.dto.SubmissionAddDto;
import com.edu.cqie.oj.domain.entity.ContestEntity;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.domain.vo.SubmissionListVo;
import com.edu.cqie.oj.mapper.ContestMapper;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.edu.cqie.oj.mapper.ProblemMapper;
import com.edu.cqie.oj.service.IHydroJudgeService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.cqie.oj.service.ISubmissionService;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.mapper.SubmissionMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;

import static com.edu.cqie.oj.domain.entity.table.SubmissionEntityTableDef.SUBMISSION_ENTITY;

/**
 * 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Slf4j
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, SubmissionEntity> implements ISubmissionService {

    @Autowired
    private IHydroJudgeService hydroJudgeService;
    
    @Resource
    private ProblemMapper problemMapper;
    
    @Resource
    private ContestMapper contestMapper;
    
    @Resource
    private ContestProblemMapper contestProblemMapper;

    /**
     * 新增提交记录
     *
     * @param submissionAddDto 提交记录添加DTO
     * @return boolean
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean addSubmission(SubmissionAddDto submissionAddDto) {
        // 验证题目是否存在
        ProblemEntity problem = problemMapper.selectOneById(submissionAddDto.getProblemId());
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }
        
        // 如果指定了比赛ID，验证比赛是否存在且题目属于该比赛
        if (submissionAddDto.getContestId() != null) {
            ContestEntity contest = contestMapper.selectOneById(submissionAddDto.getContestId());
            if (contest == null) {
                throw new RuntimeException("比赛不存在");
            }
            
            // 验证比赛时间：只能在比赛进行中提交
            Date now = new Date();
            if (now.before(contest.getStartTime())) {
                throw new RuntimeException("比赛尚未开始");
            }
            if (now.after(contest.getEndTime())) {
                throw new RuntimeException("比赛已结束");
            }
            
            // 验证题目是否属于该比赛
            QueryWrapper queryWrapper = QueryWrapper.create()
                .where("contest_id = ? AND problem_id = ?", 
                       submissionAddDto.getContestId(), 
                       submissionAddDto.getProblemId());
            ContestProblemEntity contestProblem = contestProblemMapper.selectOneByQuery(queryWrapper);
            if (contestProblem == null) {
                throw new RuntimeException("该题目不属于此比赛");
            }
        }
        
        SubmissionEntity submission = new SubmissionEntity();
        BeanUtils.copyProperties(submissionAddDto, submission);
        // 设置初始状态为判题中
        submission.setStatus(1);
        submission.setSubmittedAt(new Date());
        
        // 保存提交记录
        boolean saved = save(submission);
        if (!saved) {
            log.error("保存提交记录失败");
            return false;
        }
        
        // 提交到 HydroOJ 进行判题
        try {
            // 获取题目的Hydro题目ID
            String hydroPid = problem.getHydroProblemId();
            if (hydroPid == null || hydroPid.isEmpty()) {
                // 如果没有设置Hydro题目ID，使用本地ID
                hydroPid = String.valueOf(submission.getProblemId());
                log.warn("题目 {} 未设置Hydro题目ID，使用本地ID: {}", submission.getProblemId(), hydroPid);
            }
            
            // 调用 HydroOJ API 提交代码
            HydroJudgeResponse response = hydroJudgeService.submitCode(
                    hydroPid,
                    submission.getLanguage(),
                    submission.getCode()
            );
            
            // 保存外部提交ID
            submission.setExternalSubmissionId(response.getRid());
            updateById(submission);
            
            log.info("提交到 HydroOJ 成功，externalSubmissionId={}", response.getRid());
            
        } catch (Exception e) {
            log.error("提交到 HydroOJ 失败，将状态设为失败", e);
            // 提交失败，设置为失败状态
            submission.setStatus(3);
            // judge_result 必须是有效的 JSON 格式
            submission.setJudgeResult("{\"error\":\"提交到判题系统失败: " + e.getMessage().replace("\"", "\\\"") + "\"}");
            updateById(submission);
        }
        
        return true;
    }

    /**
     * 获取提交记录列表
     *
     * @return List<SubmissionListVo>
     */
    @Override
    public List<SubmissionListVo> getSubmissionList() {
        // 获取当前用户
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        
        QueryWrapper queryWrapper = QueryWrapper.create();
        
        // 如果不是管理员或教师，只显示自己的提交
        if (currentUser != null && !currentUser.hasPermission("submission:update")) {
            queryWrapper.where(SUBMISSION_ENTITY.USER_ID.eq(currentUser.getUserId()));
        }
        
        List<SubmissionEntity> list = list(queryWrapper);
        return list.stream().map(entity -> {
            SubmissionListVo vo = new SubmissionListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取提交记录详情
     *
     * @param id 提交记录ID
     * @return SubmissionListVo
     */
    @Override
    public SubmissionListVo getSubmissionById(Integer id) {
        SubmissionEntity entity = getById(id);
        if (entity != null) {
            SubmissionListVo vo = new SubmissionListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }

    /**
     * 根据用户ID获取提交记录列表
     *
     * @param userId 用户ID
     * @return List<SubmissionListVo>
     */
    @Override
    public List<SubmissionListVo> getSubmissionByUserId(Integer userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SUBMISSION_ENTITY.USER_ID.eq(userId))
                .orderBy(SUBMISSION_ENTITY.SUBMITTED_AT.desc());
        List<SubmissionEntity> list = list(queryWrapper);
        return list.stream().map(entity -> {
            SubmissionListVo vo = new SubmissionListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据题目ID获取提交记录列表
     *
     * @param problemId 题目ID
     * @return List<SubmissionListVo>
     */
    @Override
    public List<SubmissionListVo> getSubmissionByProblemId(Integer problemId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SUBMISSION_ENTITY.PROBLEM_ID.eq(problemId))
                .orderBy(SUBMISSION_ENTITY.SUBMITTED_AT.desc());
        List<SubmissionEntity> list = list(queryWrapper);
        return list.stream().map(entity -> {
            SubmissionListVo vo = new SubmissionListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 更新提交记录状态
     *
     * @param id 提交记录ID
     * @param status 状态
     * @param executeTime 执行时间
     * @param executeMemory 内存使用
     * @param judgeResult 判题结果（JSON格式字符串）
     * @return boolean
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean updateSubmissionStatus(Integer id, Integer status, Integer executeTime, Integer executeMemory, String judgeResult) {
        SubmissionEntity submission = getById(id);
        if (submission == null) {
            return false;
        }
        
        Integer oldStatus = submission.getStatus();
        submission.setStatus(status);
        if (executeTime != null) {
            submission.setExecuteTime(executeTime);
        }
        if (executeMemory != null) {
            submission.setExecuteMemory(executeMemory);
        }
        if (judgeResult != null) {
            submission.setJudgeResult(judgeResult);
        }
        
        boolean updated = updateById(submission);
        
        // 更新题目统计信息
        if (updated && oldStatus != status) {
            ProblemEntity problem = problemMapper.selectOneById(submission.getProblemId());
            if (problem != null) {
                boolean needUpdate = false;
                
                // 增加提交次数（只在首次判题完成时增加）
                if ((oldStatus == 0 || oldStatus == 1) && (status == 2 || status == 3)) {
                    problem.setSubmissionCount(problem.getSubmissionCount() + 1);
                    needUpdate = true;
                }
                
                // 如果从不通过变为通过，增加通过次数
                if (status == 2 && (oldStatus == 0 || oldStatus == 1 || oldStatus == 3)) {
                    problem.setAcceptedCount(problem.getAcceptedCount() + 1);
                    needUpdate = true;
                }
                
                if (needUpdate) {
                    problemMapper.update(problem);
                    log.info("更新题目统计: problemId={}, submissionCount={}, acceptedCount={}", 
                            problem.getId(), problem.getSubmissionCount(), problem.getAcceptedCount());
                }
            }
        }
        
        return updated;
    }

    /**
     * 删除提交记录
     *
     * @param id 提交记录ID
     * @return boolean
     */
    @Override
    public boolean deleteSubmission(Integer id) {
        return removeById(id);
    }

    /**
     * 同步 HydroOJ 判题结果
     *
     * @param submissionId 本地提交记录ID
     * @return boolean
     */
    @Override
    public boolean syncJudgeResult(Integer submissionId) {
        SubmissionEntity submission = getById(submissionId);
        if (submission == null) {
            log.error("提交记录不存在: id={}", submissionId);
            return false;
        }

        if (submission.getExternalSubmissionId() == null) {
            log.error("外部提交ID为空: id={}", submissionId);
            return false;
        }

        // 如果已经判题完成，不需要同步
        if (submission.getStatus() != null && (submission.getStatus() == 2 || submission.getStatus() == 3)) {
            log.debug("提交已判题完成，无需同步: id={}, status={}", submissionId, submission.getStatus());
            return true;
        }

        try {
            // 查询 HydroOJ 判题状态
            com.edu.cqie.oj.domain.dto.HydroStatusResponse statusResponse = 
                    hydroJudgeService.querySubmissionStatus(submission.getExternalSubmissionId());

            // 转换状态码
            Integer localStatus = hydroJudgeService.convertToLocalStatus(statusResponse.getStatus());
            
            // 更新状态
            submission.setStatus(localStatus);
            
            // 更新执行时间和内存（如果有）
            if (statusResponse.getTime() != null) {
                submission.setExecuteTime(statusResponse.getTime().intValue());
            }
            if (statusResponse.getMemory() != null) {
                submission.setExecuteMemory(statusResponse.getMemory().intValue());
            }
            
            // 保存判题详情（转换为 JSON 字符串）
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String judgeResultJson = objectMapper.writeValueAsString(statusResponse);
            submission.setJudgeResult(judgeResultJson);
            
            updateById(submission);
            log.info("同步判题结果成功: id={}, status={}, time={}, memory={}", 
                submissionId, localStatus, statusResponse.getTime(), statusResponse.getMemory());
            return true;
        } catch (Exception e) {
            log.error("同步判题结果失败: id={}", submissionId, e);
            return false;
        }
    }

    /**
     * 同步所有等待中的提交记录
     *
     * @return 同步成功的数量
     */
    @Override
    public int syncAllPendingSubmissions() {
        // 查询所有等待中或判题中的提交记录
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SUBMISSION_ENTITY.STATUS.in(0, 1))
                .and(SUBMISSION_ENTITY.EXTERNAL_SUBMISSION_ID.isNotNull())
                .orderBy(SUBMISSION_ENTITY.SUBMITTED_AT.asc());
        
        List<SubmissionEntity> pendingSubmissions = list(queryWrapper);
        log.info("找到 {} 条待同步的提交记录", pendingSubmissions.size());
        
        int successCount = 0;
        for (SubmissionEntity submission : pendingSubmissions) {
            try {
                if (syncJudgeResult(submission.getId())) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("同步提交记录失败: id={}", submission.getId(), e);
            }
        }
        
        log.info("同步完成: 成功 {}/{}", successCount, pendingSubmissions.size());
        return successCount;
    }

    /**
     * 定时任务：每5秒自动同步一次所有等待中的提交记录
     * 此方法会自动执行，无需手动调用
     */
    @Scheduled(fixedDelay = 5000) // 每次执行完成后间隔5秒
    public void autoSyncPendingSubmissions() {
        try {
            // 查询所有等待中或判题中的提交记录（只查最近30分钟内的，避免处理太旧的记录）
            Date thirtyMinutesAgo = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .where(SUBMISSION_ENTITY.STATUS.in(0, 1))
                    .and(SUBMISSION_ENTITY.EXTERNAL_SUBMISSION_ID.isNotNull())
                    .and(SUBMISSION_ENTITY.SUBMITTED_AT.ge(thirtyMinutesAgo))
                    .orderBy(SUBMISSION_ENTITY.SUBMITTED_AT.asc());
            
            List<SubmissionEntity> pendingSubmissions = list(queryWrapper);
            
            if (pendingSubmissions.isEmpty()) {
                return; // 没有待同步的记录，直接返回
            }
            
            log.debug("定时任务开始同步 {} 条待判题记录", pendingSubmissions.size());
            
            int successCount = 0;
            for (SubmissionEntity submission : pendingSubmissions) {
                try {
                    if (syncJudgeResult(submission.getId())) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("定时任务同步失败: id={}", submission.getId(), e);
                }
            }
            
            if (successCount > 0) {
                log.info("定时任务同步完成: 成功 {}/{}", successCount, pendingSubmissions.size());
            }
        } catch (Exception e) {
            log.error("定时任务执行异常", e);
        }
    }
}
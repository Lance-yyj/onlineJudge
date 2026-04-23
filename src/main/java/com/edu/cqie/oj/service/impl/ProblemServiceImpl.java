package com.edu.cqie.oj.service.impl;


import com.alibaba.fastjson2.JSON;
import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.dto.ProblemAddDto;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.domain.vo.ProblemListVo;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.edu.cqie.oj.mapper.SubmissionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.cqie.oj.service.IProblemService;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.mapper.ProblemMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis缓存键名和过期时间（秒）
    private static final String PROBLEM_LIST_CACHE_KEY = "problem:list";
    private static final long CACHE_EXPIRE_TIME = 300; // 5分钟

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
            
        // 从 UserContext获取当前用户ID作为创建者
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未登录，请先登录");
        }
        problem.setCreatorId(currentUserId);
            
        // 处理Hydro题目ID同步
        // 如果没有指定hydroProblemId，默认使用本地题目ID（需要先保存获取ID）
        String hydroProblemId = problemAddDto.getHydroProblemId();
        if (hydroProblemId == null || hydroProblemId.isEmpty()) {
            // 先保存获取自增ID
            problem.setCreatedAt(new Date());
            problem.setUpdatedAt(new Date());
            if (problem.getSubmissionCount() == null) {
                problem.setSubmissionCount(0);
            }
            if (problem.getAcceptedCount() == null) {
                problem.setAcceptedCount(0);
            }
            boolean saved = save(problem);
            if (!saved) {
                throw new RuntimeException("题目保存失败");
            }
            // 使用本地ID作为Hydro题目ID
            problem.setHydroProblemId(String.valueOf(problem.getId()));
            boolean updated = updateById(problem);
            // 清除缓存
            clearProblemListCache();
            return updated;
        } else {
            // 使用指定的Hydro题目ID
            problem.setHydroProblemId(hydroProblemId);
            problem.setCreatedAt(new Date());
            problem.setUpdatedAt(new Date());
            if (problem.getSubmissionCount() == null) {
                problem.setSubmissionCount(0);
            }
            if (problem.getAcceptedCount() == null) {
                problem.setAcceptedCount(0);
            }
            boolean saved = save(problem);
            // 清除缓存
            if (saved) {
                clearProblemListCache();
            }
            return saved;
        }
    }

    /**
     * 获取题目列表
     *
     * @return List<ProblemListVo>
     */
    @Override
    public List<ProblemListVo> getProblemList() {
        // 尝试从Redis缓存中获取
        try {
            Object cachedData = redisTemplate.opsForValue().get(PROBLEM_LIST_CACHE_KEY);
            if (cachedData != null) {
                // 如果缓存中存在，直接返回
                String jsonStr = cachedData.toString();
                return JSON.parseArray(jsonStr, ProblemListVo.class);
            }
        } catch (Exception e) {
            // Redis异常时记录日志但不影响业务，继续从数据库查询
            System.err.println("Redis缓存读取失败: " + e.getMessage());
        }
        
        // 缓存中没有数据，从数据库查询
        // 获取当前用户
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        
        QueryWrapper queryWrapper = QueryWrapper.create();
        
        // 如果不是管理员或教师，只显示可见的题目
        if (currentUser == null || !currentUser.hasPermission("problem:update")) {
            queryWrapper.where(PROBLEM_ENTITY.VISIBLE.eq(1));
        }
        
        List<ProblemEntity> list = list(queryWrapper);
        List<ProblemListVo> result = list.stream().map(entity -> {
            ProblemListVo vo = new ProblemListVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 将查询结果存入Redis缓存
        try {
            String jsonStr = JSON.toJSONString(result);
            redisTemplate.opsForValue().set(PROBLEM_LIST_CACHE_KEY, jsonStr, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis异常时记录日志但不影响业务
            System.err.println("Redis缓存写入失败: " + e.getMessage());
        }
        
        return result;
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
        boolean updated = updateById(problem);
        // 清除缓存
        if (updated) {
            clearProblemListCache();
        }
        return updated;
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
        
        boolean deleted = removeById(id);
        // 清除缓存
        if (deleted) {
            clearProblemListCache();
        }
        return deleted;
    }

    /**
     * 同步题目的Hydro OJ ID
     *
     * @param problemId 本地题目ID
     * @param hydroProblemId Hydro OJ题目ID
     * @return boolean
     */
    @Override
    public boolean syncHydroProblemId(Integer problemId, String hydroProblemId) {
        if (problemId == null || hydroProblemId == null || hydroProblemId.isEmpty()) {
            throw new RuntimeException("参数不能为空");
        }
        
        ProblemEntity problem = getById(problemId);
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }
        
        problem.setHydroProblemId(hydroProblemId);
        problem.setUpdatedAt(new Date());
        boolean updated = updateById(problem);
        // 清除缓存
        if (updated) {
            clearProblemListCache();
        }
        return updated;
    }
    
    /**
     * 清除题目列表缓存
     */
    private void clearProblemListCache() {
        try {
            redisTemplate.delete(PROBLEM_LIST_CACHE_KEY);
        } catch (Exception e) {
            // Redis异常时记录日志但不影响业务
            System.err.println("Redis缓存清除失败: " + e.getMessage());
        }
    }
}
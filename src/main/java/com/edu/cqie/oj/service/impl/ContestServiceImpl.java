package com.edu.cqie.oj.service.impl;


import com.edu.cqie.oj.domain.dto.ContestAddDto;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import com.edu.cqie.oj.domain.entity.SubmissionEntity;
import com.edu.cqie.oj.domain.vo.ContestListVo;
import com.edu.cqie.oj.mapper.ContestProblemMapper;
import com.edu.cqie.oj.mapper.SubmissionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 新增比赛
     *
     * @param contestAddDto 比赛添加DTO
     * @return boolean
     */
    @Override
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
        // 设置初始状态为未开始
        contest.setStatus(0);
        contest.setCreatedAt(new Date());
        contest.setUpdatedAt(new Date());
        return save(contest);
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
     * @param contest 比赛实体
     * @return boolean
     */
    @Override
    public boolean updateContest(ContestEntity contest) {
        contest.setUpdatedAt(new Date());
        return updateById(contest);
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
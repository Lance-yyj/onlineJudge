package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.ContestAddDto;
import com.edu.cqie.oj.domain.dto.ContestUpdateDto;
import com.edu.cqie.oj.domain.entity.ContestEntity;
import com.edu.cqie.oj.domain.vo.ContestListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IContestService extends IService<ContestEntity> {

    /**
     * 新增比赛
     *
     * @param contestAddDto 比赛添加DTO
     * @return boolean
     */
    boolean addContest(ContestAddDto contestAddDto);

    /**
     * 获取比赛列表
     *
     * @return List<ContestListVo>
     */
    List<ContestListVo> getContestList();

    /**
     * 根据ID获取比赛详情
     *
     * @param id 比赛ID
     * @return ContestListVo
     */
    ContestListVo getContestById(Integer id);

    /**
     * 更新比赛
     *
     * @param contestUpdateDto 比赛更新DTO
     * @return boolean
     */
    boolean updateContest(ContestUpdateDto contestUpdateDto);

    /**
     * 删除比赛
     *
     * @param id 比赛ID
     * @return boolean
     */
    boolean deleteContest(Integer id);
}
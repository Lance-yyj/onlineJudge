package com.edu.cqie.oj.service;


import com.edu.cqie.oj.domain.dto.ProblemAddDto;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.domain.vo.ProblemListVo;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IProblemService extends IService<ProblemEntity> {

    /**
     * 新增题目
     *
     * @param problemAddDto 题目添加DTO
     * @return boolean
     */
    boolean addProblem(ProblemAddDto problemAddDto);

    /**
     * 获取题目列表
     *
     * @return List<ProblemListVo>
     */
    List<ProblemListVo> getProblemList();

    /**
     * 根据ID获取题目详情
     *
     * @param id 题目ID
     * @return ProblemListVo
     */
    ProblemListVo getProblemById(Integer id);

    /**
     * 更新题目
     *
     * @param problem 题目实体
     * @return boolean
     */
    boolean updateProblem(ProblemEntity problem);

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return boolean
     */
    boolean deleteProblem(Integer id);

    /**
     * 同步题目的Hydro OJ ID
     *
     * @param problemId 本地题目ID
     * @param hydroProblemId Hydro OJ题目ID
     * @return boolean
     */
    boolean syncHydroProblemId(Integer problemId, String hydroProblemId);
}
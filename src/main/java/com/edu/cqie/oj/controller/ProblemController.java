package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.ProblemAddDto;
import com.edu.cqie.oj.domain.entity.ProblemEntity;
import com.edu.cqie.oj.domain.vo.ProblemListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IProblemService;

import java.util.List;

/**
 * 题目管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/problem")
public class ProblemController extends BaseController{

    @Resource
    private IProblemService problemService;

    /**
     * 新增题目
     *
     * @param problemAddDto 题目添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addProblem")
    @RequiresPermission("problem:create")
    public Result<Boolean> addProblem(@RequestBody ProblemAddDto problemAddDto) {
        boolean result = problemService.addProblem(problemAddDto);
        return Result.success(result);
    }

    /**
     * 获取题目列表
     *
     * @return Result<List<ProblemListVo>>
     */
    @GetMapping("/getProblemList")
    @RequiresPermission("problem:list")
    public Result<List<ProblemListVo>> getProblemList() {
        List<ProblemListVo> list = problemService.getProblemList();
        return Result.success(list);
    }

    /**
     * 根据ID获取题目详情
     *
     * @param id 题目ID
     * @return Result<ProblemListVo>
     */
    @GetMapping("/getProblemById")
    @RequiresPermission("problem:view")
    public Result<ProblemListVo> getProblemById(Integer id) {
        ProblemListVo vo = problemService.getProblemById(id);
        return Result.success(vo);
    }

    /**
     * 更新题目
     *
     * @param problem 题目实体
     * @return Result<Boolean>
     */
    @PostMapping("/updateProblem")
    @RequiresPermission("problem:update")
    public Result<Boolean> updateProblem(@RequestBody ProblemEntity problem) {
        boolean result = problemService.updateProblem(problem);
        return Result.success(result);
    }

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return Result<Boolean>
     */
    @PostMapping("/deleteProblem")
    @RequiresPermission("problem:delete")
    public Result<Boolean> deleteProblem(Integer id) {
        boolean result = problemService.deleteProblem(id);
        return Result.success(result);
    }

    /**
     * 同步题目的Hydro OJ ID
     * 用于在Hydro后台手动创建题目后，将Hydro的题目ID同步到本地
     *
     * @param problemId 本地题目ID
     * @param hydroProblemId Hydro OJ题目ID
     * @return Result<Boolean>
     */
    @PostMapping("/syncHydroProblemId")
    @RequiresPermission("problem:update")
    public Result<Boolean> syncHydroProblemId(Integer problemId, String hydroProblemId) {
        boolean result = problemService.syncHydroProblemId(problemId, hydroProblemId);
        return Result.success(result);
    }
}
package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.ContestProblemAssignDto;
import com.edu.cqie.oj.domain.vo.ContestProblemVo;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.edu.cqie.oj.service.IContestProblemService;
import com.edu.cqie.oj.domain.entity.ContestProblemEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛题目关联控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/contestProblem")
public class ContestProblemController extends BaseController{

    @Resource
    private IContestProblemService contestProblemService;

    /**
     * 为比赛分配题目
     *
     * @param assignDto 分配DTO
     * @return Result<Boolean>
     */
    @PostMapping("/assignProblems")
    @RequiresPermission("contest:assign-problem")
    public Result<Boolean> assignProblems(@RequestBody ContestProblemAssignDto assignDto) {
        boolean result = contestProblemService.assignProblems(assignDto);
        return Result.success("分配题目成功", result);
    }

    /**
     * 获取比赛的题目列表
     *
     * @param contestId 比赛ID
     * @return Result<ContestProblemVo>
     */
    @GetMapping("/getContestProblems")
    @RequiresPermission("contest:view-problem")
    public Result<ContestProblemVo> getContestProblems(Integer contestId) {
        ContestProblemVo vo = contestProblemService.getContestProblems(contestId);
        return Result.success(vo);
    }

    /**
     * 移除比赛的指定题目
     *
     * @param contestId 比赛ID
     * @param problemId 题目ID
     * @return Result<Boolean>
     */
    @PostMapping("/removeContestProblem")
    @RequiresPermission("contest:remove-problem")
    public Result<Boolean> removeContestProblem(Integer contestId, Integer problemId) {
        boolean result = contestProblemService.removeContestProblem(contestId, problemId);
        return Result.success("移除题目成功", result);
    }
}
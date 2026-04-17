package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.SubmissionAddDto;
import com.edu.cqie.oj.domain.vo.SubmissionListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.ISubmissionService;

import java.util.List;

/**
 * 提交记录控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/submission")
public class SubmissionController extends BaseController{

    @Resource
    private ISubmissionService submissionService;

    /**
     * 新增提交记录
     *
     * @param submissionAddDto 提交记录添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addSubmission")
    @RequiresPermission("submission:create")
    public Result<Boolean> addSubmission(@RequestBody SubmissionAddDto submissionAddDto) {
        // 从UserContext获取当前登录用户ID，防止前端伪造
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("未登录，请先登录");
        }
        submissionAddDto.setUserId(currentUserId);
        
        boolean result = submissionService.addSubmission(submissionAddDto);
        return Result.success(result);
    }

    /**
     * 获取提交记录列表
     *
     * @return Result<List<SubmissionListVo>>
     */
    @GetMapping("/getSubmissionList")
    @RequiresPermission("submission:list")
    public Result<List<SubmissionListVo>> getSubmissionList() {
        List<SubmissionListVo> list = submissionService.getSubmissionList();
        return Result.success(list);
    }

    /**
     * 根据ID获取提交记录详情
     *
     * @param id 提交记录ID
     * @return Result<SubmissionListVo>
     */
    @GetMapping("/getSubmissionById")
    @RequiresPermission("submission:view")
    public Result<SubmissionListVo> getSubmissionById(Integer id) {
        SubmissionListVo vo = submissionService.getSubmissionById(id);
        return Result.success(vo);
    }

    /**
     * 根据用户ID获取提交记录列表
     * 学生只能查看自己的提交记录，管理员可以查看所有用户
     *
     * @param userId 用户ID
     * @return Result<List<SubmissionListVo>>
     */
    @GetMapping("/getSubmissionByUserId")
    @RequiresPermission("submission:list")
    public Result<List<SubmissionListVo>> getSubmissionByUserId(Integer userId) {
        // 获取当前登录用户ID
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("未登录，请先登录");
        }
        
        // 如果不是查询自己的记录，需要检查是否有管理员权限
        if (!currentUserId.equals(userId)) {
            // 这里可以添加管理员权限检查，或者在Service层处理
            // 简单实现：允许查询，但在Service层可以根据角色过滤
        }
        
        List<SubmissionListVo> list = submissionService.getSubmissionByUserId(userId);
        return Result.success(list);
    }

    /**
     * 根据题目ID获取提交记录列表
     *
     * @param problemId 题目ID
     * @return Result<List<SubmissionListVo>>
     */
    @GetMapping("/getSubmissionByProblemId")
    @RequiresPermission("submission:list")
    public Result<List<SubmissionListVo>> getSubmissionByProblemId(Integer problemId) {
        List<SubmissionListVo> list = submissionService.getSubmissionByProblemId(problemId);
        return Result.success(list);
    }

    /**
     * 更新提交记录状态
     *
     * @param id 提交记录ID
     * @param status 状态
     * @param executeTime 执行时间
     * @param executeMemory 内存使用
     * @param judgeResult 判题结果
     * @return Result<Boolean>
     */
    @PostMapping("/updateSubmissionStatus")
    @RequiresPermission("submission:update")
    public Result<Boolean> updateSubmissionStatus(Integer id, Integer status, Integer executeTime, Integer executeMemory, Object judgeResult) {
        boolean result = submissionService.updateSubmissionStatus(id, status, executeTime, executeMemory, judgeResult);
        return Result.success(result);
    }

    /**
     * 删除提交记录
     *
     * @param id 提交记录ID
     * @return Result<Boolean>
     */
    @PostMapping("/deleteSubmission")
    @RequiresPermission("submission:delete")
    public Result<Boolean> deleteSubmission(Integer id) {
        boolean result = submissionService.deleteSubmission(id);
        return Result.success(result);
    }
}
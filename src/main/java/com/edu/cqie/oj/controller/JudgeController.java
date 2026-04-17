package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.service.impl.SubmissionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 判题控制器
 *
 * @author system
 * @since 1.0
 */
@RestController
@RequestMapping("/api/judge")
public class JudgeController extends BaseController {

    @Autowired
    private SubmissionServiceImpl submissionService;

    /**
     * 同步判题结果
     * 仅管理员可调用（内部服务调用也应使用管理员Token）
     *
     * @param submissionId 提交记录ID
     * @return 同步结果
     */
    @PostMapping("/sync/{submissionId}")
    @RequiresPermission("submission:update")
    public Result<Boolean> syncJudgeResult(@PathVariable Integer submissionId) {
        boolean success = submissionService.syncJudgeResult(submissionId);
        if (success) {
            return Result.success("同步成功");
        } else {
            return Result.error("同步失败或判题尚未完成");
        }
    }
}

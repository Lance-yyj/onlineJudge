package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.service.impl.SubmissionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 判题控制器
 *
 * @author system
 * @since 1.0
 */
@Tag(name = "判题管理")
@RestController
@RequestMapping("/api/judge")
public class JudgeController extends BaseController {

    @Autowired
    private SubmissionServiceImpl submissionService;

    /**
     * 同步判题结果
     *
     * @param submissionId 提交记录ID
     * @return 同步结果
     */
    @Operation(summary = "同步判题结果")
    @PostMapping("/sync/{submissionId}")
    public Result<Boolean> syncJudgeResult(@PathVariable Integer submissionId) {
        boolean success = submissionService.syncJudgeResult(submissionId);
        if (success) {
            return Result.success("同步成功");
        } else {
            return Result.error("同步失败或判题尚未完成");
        }
    }
}

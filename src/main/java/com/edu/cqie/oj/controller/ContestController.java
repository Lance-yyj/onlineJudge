package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.ContestAddDto;
import com.edu.cqie.oj.domain.dto.ContestUpdateDto;
import com.edu.cqie.oj.domain.entity.ContestEntity;
import com.edu.cqie.oj.domain.vo.ContestListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.cqie.oj.service.IContestService;

import java.util.List;

/**
 * 比赛管理控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/contest")
public class ContestController extends BaseController{

    @Resource
    private IContestService contestService;

    /**
     * 新增比赛
     *
     * @param contestAddDto 比赛添加DTO
     * @return Result<Boolean>
     */
    @PostMapping("/addContest")
    @RequiresPermission("contest:create")
    public Result<Boolean> addContest(@RequestBody ContestAddDto contestAddDto) {
        boolean result = contestService.addContest(contestAddDto);
        return Result.success(result);
    }

    /**
     * 获取比赛列表
     *
     * @return Result<List<ContestListVo>>
     */
    @GetMapping("/getContestList")
    @RequiresPermission("contest:list")
    public Result<List<ContestListVo>> getContestList() {
        List<ContestListVo> list = contestService.getContestList();
        return Result.success(list);
    }

    /**
     * 根据ID获取比赛详情
     *
     * @param id 比赛ID
     * @param password 比赛密码（私有比赛需要）
     * @return Result<ContestListVo>
     */
    @GetMapping("/getContestById")
    @RequiresPermission("contest:view")
    public Result<ContestListVo> getContestById(Integer id, String password) {
        com.edu.cqie.oj.domain.entity.ContestEntity contest = contestService.getById(id);
        if (contest == null) {
            return Result.error("比赛不存在");
        }
        
        // 如果是私有比赛，验证密码
        if (contest.getContestType() != null && contest.getContestType() == 2) {
            if (password == null || password.isEmpty()) {
                return Result.error("该比赛为私有比赛，需要输入密码");
            }
            if (!password.equals(contest.getPassword())) {
                return Result.error("比赛密码错误");
            }
        }
        
        ContestListVo vo = contestService.getContestById(id);
        return Result.success(vo);
    }

    /**
     * 更新比赛
     *
     * @param contestUpdateDto 比赛更新DTO
     * @return Result<Boolean>
     */
    @PostMapping("/updateContest")
    @RequiresPermission("contest:update")
    public Result<Boolean> updateContest(@RequestBody ContestUpdateDto contestUpdateDto) {
        boolean result = contestService.updateContest(contestUpdateDto);
        return Result.success(result);
    }

    /**
     * 删除比赛
     *
     * @param id 比赛ID
     * @return Result<Boolean>
     */
    @PostMapping("/deleteContest")
    @RequiresPermission("contest:delete")
    public Result<Boolean> deleteContest(Integer id) {
        boolean result = contestService.deleteContest(id);
        return Result.success(result);
    }

    /**
     * 获取比赛总数
     *
     * @return Result<Long>
     */
    @GetMapping("/getContestCount")
    @RequiresPermission("contest:list")
    public Result<Long> getContestCount() {
        long count = contestService.count();
        return Result.success(count);
    }
}
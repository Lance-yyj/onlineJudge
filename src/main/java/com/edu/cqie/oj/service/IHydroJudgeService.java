package com.edu.cqie.oj.service;

import com.edu.cqie.oj.domain.dto.HydroJudgeResponse;
import com.edu.cqie.oj.domain.dto.HydroStatusResponse;

/**
 * HydroOJ 判题服务接口
 *
 * @author system
 * @since 1.0
 */
public interface IHydroJudgeService {

    /**
     * 提交代码到 HydroOJ 进行判题
     *
     * @param pid     题目ID
     * @param language 编程语言
     * @param code     源代码
     * @return 判题响应
     */
    HydroJudgeResponse submitCode(String pid, String language, String code);

    /**
     * 查询提交状态
     *
     * @param rid 提交ID
     * @return 状态响应
     */
    HydroStatusResponse querySubmissionStatus(String rid);

    /**
     * 将本地语言映射为 HydroOJ 语言ID
     *
     * @param localLanguage 本地语言名称
     * @return HydroOJ 语言ID
     */
    String mapLanguage(String localLanguage);

    /**
     * 将 HydroOJ 状态码转换为本地状态
     *
     * @param hydroStatus HydroOJ 状态码
     * @return 本地状态码（0-等待中，1-判题中，2-通过，3-失败）
     */
    Integer convertToLocalStatus(Integer hydroStatus);
}

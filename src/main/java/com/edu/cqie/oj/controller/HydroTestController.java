package com.edu.cqie.oj.controller;

import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.domain.dto.HydroJudgeResponse;
import com.edu.cqie.oj.domain.dto.HydroStatusResponse;
import com.edu.cqie.oj.service.IHydroJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * HydroOJ 判题系统测试控制器
 * 用于测试 Hydro API 的连通性和功能
 *
 * @author system
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test/hydro")
public class HydroTestController extends BaseController {

    @Autowired
    private IHydroJudgeService hydroJudgeService;

    /**
     * 测试 HydroOJ API 连通性
     *
     * @return 测试结果
     */
    @GetMapping("/connectivity")
    public Result<Map<String, Object>> testConnectivity() {
        log.info("开始测试 HydroOJ API 连通性");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用一个简单的 A+B 题目代码进行测试
            String testCode = "#include <stdio.h>\n" +
                    "int main() {\n" +
                    "    return 0;\n" +
                    "}";

            log.info("尝试提交测试代码到 HydroOJ");
            
            // 注意：这里需要替换为你 HydroOJ 中实际存在的题目ID
            // 如果还没有题目，请先在 HydroOJ 中创建一个测试题目
            HydroJudgeResponse response = hydroJudgeService.submitCode(
                    "1",  // 题目ID，需要根据实际情况修改
                    "C",   // 语言
                    testCode
            );

            result.put("success", true);
            result.put("message", "API 连通性测试成功");
            result.put("submissionId", response.getRid());
            result.put("response", response);

            log.info("连通性测试成功，提交ID: {}", response.getRid());
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("API 连通性测试失败", e);
            
            result.put("success", false);
            result.put("message", "API 连通性测试失败: " + e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("stackTrace", e.toString());
            
            // 如果有 cause，也输出
            if (e.getCause() != null) {
                result.put("cause", e.getCause().toString());
            }

            return Result.error("API 连通性测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试查询提交状态
     *
     * @param rid 提交记录ID
     * @return 查询结果
     */
    @GetMapping("/status/{rid}")
    public Result<Map<String, Object>> testQueryStatus(@PathVariable String rid) {
        log.info("测试查询提交状态: rid={}", rid);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            HydroStatusResponse statusResponse = hydroJudgeService.querySubmissionStatus(rid);

            result.put("success", true);
            result.put("message", "状态查询成功");
            result.put("status", statusResponse);
            result.put("localStatus", hydroJudgeService.convertToLocalStatus(statusResponse.getStatus()));

            log.info("状态查询成功: {}", statusResponse);
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("状态查询失败", e);
            
            result.put("success", false);
            result.put("message", "状态查询失败: " + e.getMessage());

            return Result.error("状态查询失败: " + e.getMessage());
        }
    }

    /**
     * 完整的提交流程测试（提交 + 轮询状态）
     *
     * @param pid 题目ID
     * @param language 编程语言（C, C++, Java, Python等）
     * @return 测试结果
     */
    @PostMapping("/full-test")
    public Result<Map<String, Object>> testFullSubmission(
            @RequestParam String pid,
            @RequestParam(defaultValue = "C") String language) {
        
        log.info("开始完整提交流程测试: pid={}, language={}", pid, language);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据不同语言准备测试代码
            String testCode = getTestCode(language);
            
            // 1. 提交代码
            log.info("步骤1: 提交代码");
            HydroJudgeResponse submitResponse = hydroJudgeService.submitCode(pid, language, testCode);
            
            String rid = submitResponse.getRid();
            result.put("submissionId", rid);
            result.put("submitResponse", submitResponse);
            
            log.info("代码提交成功，rid={}", rid);
            
            // 2. 轮询查询状态（最多查询10次，每次间隔2秒）
            log.info("步骤2: 开始轮询查询判题状态");
            int maxAttempts = 10;
            int attempt = 0;
            HydroStatusResponse finalStatus = null;
            
            while (attempt < maxAttempts) {
                Thread.sleep(2000); // 等待2秒
                attempt++;
                
                log.info("第{}次查询状态", attempt);
                HydroStatusResponse statusResponse = hydroJudgeService.querySubmissionStatus(rid);
                
                Integer localStatus = hydroJudgeService.convertToLocalStatus(statusResponse.getStatus());
                log.info("当前状态: hydroStatus={}, localStatus={}", 
                        statusResponse.getStatus(), localStatus);
                
                // 0-等待中, 1-判题中, 2-通过, 3-失败
                if (localStatus == 2 || localStatus == 3) {
                    // 判题完成（通过或失败）
                    finalStatus = statusResponse;
                    break;
                }
            }
            
            if (finalStatus != null) {
                result.put("finalStatus", finalStatus);
                result.put("localStatus", hydroJudgeService.convertToLocalStatus(finalStatus.getStatus()));
                result.put("attempts", attempt);
                
                Integer localStatus = hydroJudgeService.convertToLocalStatus(finalStatus.getStatus());
                if (localStatus == 2) {
                    result.put("testResult", "PASSED");
                    log.info("测试通过！");
                } else {
                    result.put("testResult", "FAILED");
                    log.info("测试失败！");
                }
                
                return Result.success(result);
            } else {
                result.put("testResult", "TIMEOUT");
                result.put("message", "判题超时，请稍后手动查询状态");
                log.warn("判题超时");
                return Result.success(result);
            }

        } catch (Exception e) {
            log.error("完整提交流程测试失败", e);
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取测试代码
     */
    private String getTestCode(String language) {
        switch (language.toUpperCase()) {
            case "C":
                return "#include <stdio.h>\n" +
                        "int main() {\n" +
                        "    int a, b;\n" +
                        "    scanf(\"%d %d\", &a, &b);\n" +
                        "    printf(\"%d\\n\", a + b);\n" +
                        "    return 0;\n" +
                        "}";
            
            case "C++":
            case "CPP":
                return "#include <iostream>\n" +
                        "using namespace std;\n" +
                        "int main() {\n" +
                        "    int a, b;\n" +
                        "    cin >> a >> b;\n" +
                        "    cout << a + b << endl;\n" +
                        "    return 0;\n" +
                        "}";
            
            case "JAVA":
                return "import java.util.Scanner;\n" +
                        "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        int a = sc.nextInt();\n" +
                        "        int b = sc.nextInt();\n" +
                        "        System.out.println(a + b);\n" +
                        "    }\n" +
                        "}";
            
            case "PYTHON":
            case "PYTHON3":
                return "a, b = map(int, input().split())\n" +
                        "print(a + b)";
            
            default:
                throw new IllegalArgumentException("不支持的语言: " + language);
        }
    }
}

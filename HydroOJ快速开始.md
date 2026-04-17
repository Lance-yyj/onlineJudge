# HydroOJ 判题 API 快速开始

## 📋 前置条件

1. 已部署 HydroOJ 系统（参考：https://github.com/hydro-dev/Hydro）
2. 获取 HydroOJ API Token

## 🚀 快速开始

### 第一步：配置 HydroOJ 连接

编辑 `src/main/resources/application.yaml`：

```yaml
hydro:
  judge:
    # 修改为你的 HydroOJ 地址
    base-url: http://your-hydro-server:8888
    # 修改为你的 API Token
    api-token: your-actual-api-token
```

### 第二步：启动应用

```bash
mvn spring-boot:run
```

### 第三步：提交代码测试

#### 方式一：通过 SubmissionController

```bash
curl -X POST http://localhost:8080/api/submission \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "problemId": 1001,
    "code": "#include <stdio.h>\nint main() {\n  printf(\"Hello World\");\n  return 0;\n}",
    "language": "C++"
  }'
```

#### 方式二：通过代码调用

```java
@Autowired
private ISubmissionService submissionService;

SubmissionAddDto dto = new SubmissionAddDto();
dto.setUserId(1);
dto.setProblemId(1001);
dto.setCode("#include <stdio.h>\nint main() { ... }");
dto.setLanguage("C++");

submissionService.addSubmission(dto);
```

### 第四步：查看判题结果

#### 方式一：查询提交记录

```bash
curl http://localhost:8080/api/submission/1
```

#### 方式二：手动同步结果

```bash
curl -X POST http://localhost:8080/api/judge/sync/1
```

## 📊 工作流程

```
用户提交代码
    ↓
保存本地记录（状态：等待中）
    ↓
更新状态为"判题中"
    ↓
调用 HydroOJ API 提交代码
    ↓
保存外部提交ID
    ↓
[异步] HydroOJ 进行判题
    ↓
[可选] 手动同步或等待回调
    ↓
更新本地状态和结果
```

## 🔧 常见问题

### Q1: 如何获取 HydroOJ API Token?

A: 登录 HydroOJ 管理后台 → 设置 → API 令牌 → 创建新令牌

### Q2: 支持哪些编程语言?

A: C, C++, Java, Python, Python3, JavaScript, Go, Rust

### Q3: 判题结果是实时更新的吗?

A: 当前实现需要手动调用同步接口。生产环境建议：
- 实现 Webhook 回调
- 或使用定时任务轮询

### Q4: 如何处理判题失败?

A: 系统会自动捕获异常并记录日志，提交状态会设为"失败"。可以：
1. 查看日志排查问题
2. 检查 HydroOJ 服务状态
3. 验证网络连接

## 📝 示例代码

### 完整提交流程

```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private ISubmissionService submissionService;
    
    @Autowired
    private SubmissionServiceImpl submissionServiceImpl;
    
    @PostMapping("/submit")
    public Result<?> submitCode() {
        // 1. 提交代码
        SubmissionAddDto dto = new SubmissionAddDto();
        dto.setUserId(1);
        dto.setProblemId(1001);
        dto.setCode("#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n  cout << \"Hello\" << endl;\n  return 0;\n}");
        dto.setLanguage("C++");
        
        boolean success = submissionService.addSubmission(dto);
        
        if (success) {
            return Result.success("提交成功，正在判题...");
        } else {
            return Result.error("提交失败");
        }
    }
    
    @GetMapping("/result/{id}")
    public Result<?> getResult(@PathVariable Integer id) {
        // 2. 同步判题结果
        submissionServiceImpl.syncJudgeResult(id);
        
        // 3. 查询结果
        SubmissionListVo result = submissionService.getSubmissionById(id);
        return Result.success(result);
    }
}
```

## 🎯 下一步

1. **配置真实的 HydroOJ 服务器地址**
2. **创建 API Token 并配置**
3. **测试代码提交功能**
4. **根据需要实现异步判题或 Webhook 回调**
5. **添加定时任务自动同步判题结果**

## 📚 更多文档

- [HydroOJ集成说明.md](./HydroOJ集成说明.md) - 详细集成文档
- [RBAC使用说明.md](./RBAC使用说明.md) - 权限管理说明

## 💡 提示

- 开发环境可以使用本地的 HydroOJ 实例
- 生产环境建议使用 HTTPS
- 定期备份 API Token
- 监控判题成功率和服务可用性

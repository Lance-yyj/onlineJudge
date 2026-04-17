# HydroOJ 判题 API 集成说明

## 概述

本项目已成功集成 HydroOJ 判题系统 API，实现了代码提交、判题和结果同步功能。

## 配置说明

### 1. 配置文件

在 `application.yaml` 中配置 HydroOJ 相关参数：

```yaml
hydro:
  judge:
    # HydroOJ API 基础URL（根据实际部署地址修改）
    base-url: http://localhost:8888
    # API Token（需要在HydroOJ中创建）
    api-token: your-hydro-api-token-here
    # 连接超时时间（毫秒）
    connect-timeout: 5000
    # 读取超时时间（毫秒）
    read-timeout: 30000
    # 写入超时时间（毫秒）
    write-timeout: 30000
```

### 2. 获取 API Token

1. 登录到 HydroOJ 管理后台
2. 进入"设置" -> "API 令牌"
3. 创建新的 API Token
4. 将 Token 复制到配置文件中

## 核心组件

### 1. DTO 类

- **HydroSubmitRequest**: 代码提交请求
- **HydroJudgeResponse**: 判题结果响应
- **HydroStatusResponse**: 提交状态查询响应

### 2. 服务接口

- **IHydroJudgeService**: HydroOJ 判题服务接口
  - `submitCode()`: 提交代码进行判题
  - `querySubmissionStatus()`: 查询提交状态
  - `mapLanguage()`: 语言映射
  - `convertToLocalStatus()`: 状态码转换

### 3. 实现类

- **HydroJudgeServiceImpl**: HydroOJ 判题服务实现
- **SubmissionServiceImpl**: 提交记录服务（已集成 HydroOJ）

## 使用方式

### 1. 自动判题（推荐）

当用户提交代码时，系统会自动调用 HydroOJ API 进行判题：

```java
@Autowired
private ISubmissionService submissionService;

// 提交代码
SubmissionAddDto dto = new SubmissionAddDto();
dto.setUserId(1);
dto.setProblemId(1001);
dto.setCode("#include <stdio.h>\nint main() { ... }");
dto.setLanguage("C++");

submissionService.addSubmission(dto);
```

流程：
1. 保存本地提交记录（状态：等待中）
2. 更新状态为"判题中"
3. 调用 HydroOJ API 提交代码
4. 保存外部提交ID
5. 如果失败，状态设为"失败"

### 2. 手动同步判题结果

如果需要手动同步判题结果：

```java
@Autowired
private SubmissionServiceImpl submissionService;

// 同步判题结果
boolean success = submissionService.syncJudgeResult(submissionId);
```

### 3. 直接使用 HydroOJ 服务

```java
@Autowired
private IHydroJudgeService hydroJudgeService;

// 提交代码
HydroJudgeResponse response = hydroJudgeService.submitCode(
    "P1001",      // 题目ID
    "C++",        // 编程语言
    sourceCode    // 源代码
);

// 查询状态
HydroStatusResponse status = hydroJudgeService.querySubmissionStatus(
    response.getRid()
);
```

## 语言映射

系统支持以下编程语言映射：

| 本地语言 | HydroOJ 语言ID |
|---------|---------------|
| C       | c             |
| C++     | cc            |
| CPP     | cpp           |
| Java    | java          |
| Python  | python        |
| Python3 | python3       |
| JavaScript | js         |
| Go      | go            |
| Rust    | rust          |

## 状态码映射

| HydroOJ 状态码 | 描述 | 本地状态码 |
|--------------|------|----------|
| 0            | Waiting | 0 (等待中) |
| 1            | Accepted | 2 (通过) |
| 2            | Wrong Answer | 3 (失败) |
| 3            | Time Limit Exceeded | 3 (失败) |
| 4            | Memory Limit Exceeded | 3 (失败) |
| 5            | Output Limit Exceeded | 3 (失败) |
| 6            | Runtime Error | 3 (失败) |
| 7            | Compile Error | 3 (失败) |
| 8            | System Error | 3 (失败) |
| 9            | Cancelled | 3 (失败) |

## API 端点

HydroOJ 提供的 API 端点：

- **POST** `/api/submit`: 提交代码
- **GET** `/api/status/{rid}`: 查询提交状态

## 注意事项

1. **网络连通性**: 确保应用服务器能够访问 HydroOJ API
2. **API Token 安全**: 不要将 API Token 泄露给他人
3. **超时设置**: 根据网络情况调整超时时间
4. **异步处理**: 当前实现是同步调用，生产环境建议使用异步或消息队列
5. **错误处理**: 所有异常都已捕获并记录日志

## 扩展开发

### 添加新语言支持

在 `HydroJudgeServiceImpl` 的 `LANGUAGE_MAP` 中添加映射：

```java
static {
    LANGUAGE_MAP.put("NewLanguage", "newlang");
}
```

### 自定义状态映射

修改 `convertToLocalStatus()` 方法以支持更细粒度的状态：

```java
@Override
public Integer convertToLocalStatus(Integer hydroStatus) {
    // 自定义映射逻辑
}
```

### 实现回调机制

如果需要实时接收判题结果，可以实现 HydroOJ 的 Webhook 回调：

1. 创建回调控制器
2. 验证回调签名
3. 更新本地提交记录

## 故障排查

### 1. 连接失败

检查：
- HydroOJ 服务是否运行
- `base-url` 配置是否正确
- 网络连接是否正常

### 2. 认证失败

检查：
- `api-token` 是否正确
- Token 是否过期
- Token 权限是否足够

### 3. 判题超时

检查：
- 增加 `read-timeout` 和 `write-timeout`
- 检查 HydroOJ 服务器负载
- 查看网络延迟

## 依赖说明

项目已添加以下依赖：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>
```

## 下一步优化建议

1. **异步判题**: 使用 Spring Async 或消息队列实现异步判题
2. **结果轮询**: 定时任务轮询未完成的判题结果
3. **Webhook 支持**: 实现 HydroOJ 的回调接口
4. **缓存优化**: 缓存常用题目的判题结果
5. **负载均衡**: 支持多个 HydroOJ 实例
6. **监控告警**: 添加判题成功率监控

## 参考资料

- [HydroOJ 官方文档](https://docs.hydro.ac/)
- [HydroOJ GitHub](https://github.com/hydro-dev/Hydro)
- [OkHttp 文档](https://square.github.io/okhttp/)

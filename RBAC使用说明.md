# RBAC权限管理系统 - 使用说明

## 概述

本系统实现了基于角色的访问控制（RBAC），支持方法级别的细粒度权限管理。

## 核心组件

### 1. 数据库设计

#### 表结构
- **user**: 用户表
- **role**: 角色表
- **permission**: 权限表
- **user_role**: 用户-角色关联表
- **role_permission**: 角色-权限关联表

#### 权限代码规范
权限代码采用 `模块:操作` 的格式，例如：
- `user:create` - 创建用户
- `problem:list` - 查看题目列表
- `contest:delete` - 删除比赛

### 2. 核心类说明

#### 注解
- **@RequiresPermission**: 方法级权限注解
  - `value`: 权限代码数组
  - `requireLogin`: 是否需要登录（默认true）
  - `logical`: 逻辑关系AND/OR（默认OR）

#### 工具类
- **JwtUtil**: JWT Token生成和验证
- **UserContext**: 用户上下文持有类（ThreadLocal）

#### 拦截器
- **PermissionInterceptor**: 权限拦截器，实现方法级权限控制

#### 配置类
- **WebMvcConfig**: 注册权限拦截器

### 3. 使用方式

#### 在Controller方法上添加权限注解

```java
// 需要特定权限
@PostMapping("/addUser")
@RequiresPermission("user:create")
public Result<Boolean> addUser(@RequestBody UserAddDto userAddDto) {
    // ...
}

// 只需要登录，不需要特定权限
@PostMapping("/logout")
@RequiresPermission(requireLogin = true)
public Result<Boolean> logout(Integer userId) {
    // ...
}

// 不需要登录
@PostMapping("/login")
@RequiresPermission(value = {}, requireLogin = false)
public Result<UserLoginVo> login(@RequestBody UserLoginDto loginDto) {
    // ...
}

// 需要多个权限（OR逻辑，满足任一即可）
@RequiresPermission(value = {"user:create", "user:update"})
public Result<Boolean> saveUser(...) {
    // ...
}

// 需要多个权限（AND逻辑，必须全部满足）
@RequiresPermission(value = {"user:create", "role:assign"}, logical = Logical.AND)
public Result<Boolean> createUserWithRole(...) {
    // ...
}
```

#### 在代码中获取当前用户信息

```java
// 获取当前用户ID
Integer userId = UserContext.getCurrentUserId();

// 获取当前用户名
String username = UserContext.getCurrentUsername();

// 检查当前用户是否有某个权限
boolean hasPermission = UserContext.hasPermission("user:create");

// 获取完整用户信息
UserContext.UserInfo userInfo = UserContext.getCurrentUser();
List<String> permissions = userInfo.getPermissions();
```

### 4. 前端调用方式

#### 登录获取Token
```javascript
POST /user/login
{
  "username": "admin",
  "password": "admin123"
}

响应:
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "id": 1,
    "username": "admin",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    ...
  }
}
```

#### 携带Token访问接口
```javascript
// 在请求头中添加Authorization
headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9...'
}

GET /user/getUserList
```

### 5. 权限初始化

执行 `init_rbac_data.sql` 脚本初始化数据：

```bash
mysql -u root -p your_database < init_rbac_data.sql
```

#### 默认账号
- 用户名: `admin`
- 密码: `admin123`
- 角色: 超级管理员（拥有所有权限）

#### 预定义角色
1. **超级管理员**: 拥有所有权限
2. **教师**: 可以管理题目、比赛和查看学生提交
3. **学生**: 可以做题、参加比赛和查看自己的提交
4. **访客**: 只能查看公开信息

### 6. 权限管理流程

#### 为用户分配角色
```javascript
POST /user/assignRoles
Headers: Authorization: Bearer {token}
Body:
{
  "userId": 2,
  "roleIds": [2, 3]  // 分配教师和学生角色
}
```

#### 为角色分配权限
```javascript
POST /role/assignPermissions
Headers: Authorization: Bearer {token}
Body:
{
  "roleId": 2,
  "permissionIds": [6, 7, 8, 9, 10]  // 分配题目管理相关权限
}
```

#### 查看用户的角色
```javascript
GET /user/getUserRoles?userId=2
Headers: Authorization: Bearer {token}
```

#### 查看角色的权限
```javascript
GET /role/getRolePermissions?roleId=2
Headers: Authorization: Bearer {token}
```

### 7. 已配置的权限列表

#### 用户管理 (user)
- `user:create` - 创建用户
- `user:list` - 查看用户列表
- `user:assign-role` - 分配用户角色
- `user:view-role` - 查看用户角色
- `user:remove-role` - 移除用户角色

#### 角色管理 (role)
- `role:create` - 创建角色
- `role:list` - 查看角色列表
- `role:assign-permission` - 分配角色权限
- `role:view-permission` - 查看角色权限
- `role:remove-permission` - 移除角色权限

#### 权限管理 (permission)
- `permission:create` - 创建权限
- `permission:list` - 查看权限列表

#### 题目管理 (problem)
- `problem:create` - 创建题目
- `problem:list` - 查看题目列表
- `problem:view` - 查看题目详情
- `problem:update` - 更新题目
- `problem:delete` - 删除题目

#### 比赛管理 (contest)
- `contest:create` - 创建比赛
- `contest:list` - 查看比赛列表
- `contest:view` - 查看比赛详情
- `contest:update` - 更新比赛
- `contest:delete` - 删除比赛
- `contest:assign-problem` - 分配比赛题目
- `contest:view-problem` - 查看比赛题目
- `contest:remove-problem` - 移除比赛题目

#### 提交记录 (submission)
- `submission:create` - 创建提交
- `submission:list` - 查看提交列表
- `submission:view` - 查看提交详情
- `submission:update` - 更新提交状态
- `submission:delete` - 删除提交

#### 公告管理 (announcement)
- `announcement:create` - 创建公告
- `announcement:list` - 查看公告列表
- `announcement:view` - 查看公告详情
- `announcement:update` - 更新公告
- `announcement:delete` - 删除公告

### 8. 安全特性

1. **密码加密**: 使用BCrypt算法加密存储密码
2. **JWT Token**: 使用JWT进行身份认证，有效期24小时
3. **方法级权限**: 每个方法都可以独立配置权限
4. **ThreadLocal隔离**: 使用ThreadLocal确保线程安全
5. **自动清理**: 请求完成后自动清理用户上下文

### 9. 注意事项

1. **Token有效期**: JWT Token有效期为24小时，过期后需要重新登录
2. **密码修改**: 如果修改了用户密码，需要重新登录获取新Token
3. **权限变更**: 修改角色权限后，用户需要重新登录才能生效
4. **默认放行**: 登录接口 `/user/login` 默认放行，无需Token
5. **异常处理**: 权限不足时返回403错误，未登录返回401错误

### 10. 扩展开发

#### 添加新的权限
1. 在数据库中插入新权限记录
2. 在对应的Controller方法上添加 `@RequiresPermission` 注解
3. 为需要的角色分配该权限

#### 自定义权限逻辑
可以在 `PermissionInterceptor` 中扩展更复杂的权限逻辑，例如：
- 基于资源的权限控制
- 基于时间的权限控制
- 基于数据的权限控制

## 总结

本系统提供了完整的RBAC权限管理解决方案，具有以下特点：
- ✅ 方法级别细粒度权限控制
- ✅ JWT Token身份认证
- ✅ BCrypt密码加密
- ✅ 灵活的角色权限分配
- ✅ 简单易用的注解方式
- ✅ 完善的默认数据和角色

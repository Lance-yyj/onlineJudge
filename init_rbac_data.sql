-- =============================================
-- 在线判题系统 - RBAC权限管理初始化脚本
-- =============================================

-- 1. 插入基础角色
INSERT INTO `role` (`role_name`, `description`, `created_at`, `updated_at`) VALUES
('管理员', '拥有系统所有权限，可以管理用户、题目、比赛等', NOW(), NOW()),
('学生', '可以做题、参加比赛和查看自己的提交记录', NOW(), NOW());

-- 2. 插入权限（方法级别）
-- 用户管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建用户', '新增用户账号', 'user:create', 'user', NOW()),
('查看用户列表', '获取用户列表', 'user:list', 'user', NOW()),
('分配用户角色', '为用户分配角色', 'user:assign-role', 'user', NOW()),
('查看用户角色', '查看用户的角色列表', 'user:view-role', 'user', NOW()),
('移除用户角色', '移除用户的指定角色', 'user:remove-role', 'user', NOW());

-- 角色管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建角色', '新增角色', 'role:create', 'role', NOW()),
('查看角色列表', '获取角色列表', 'role:list', 'role', NOW()),
('分配角色权限', '为角色分配权限', 'role:assign-permission', 'role', NOW()),
('查看角色权限', '查看角色的权限列表', 'role:view-permission', 'role', NOW()),
('移除角色权限', '移除角色的指定权限', 'role:remove-permission', 'role', NOW());

-- 权限管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建权限', '新增权限', 'permission:create', 'permission', NOW()),
('查看权限列表', '获取权限列表', 'permission:list', 'permission', NOW());

-- 题目管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建题目', '新增题目', 'problem:create', 'problem', NOW()),
('查看题目列表', '获取题目列表', 'problem:list', 'problem', NOW()),
('查看题目详情', '查看题目详细信息', 'problem:view', 'problem', NOW()),
('更新题目', '修改题目', 'problem:update', 'problem', NOW()),
('删除题目', '删除题目', 'problem:delete', 'problem', NOW());

-- 比赛管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建比赛', '新增比赛', 'contest:create', 'contest', NOW()),
('查看比赛列表', '获取比赛列表', 'contest:list', 'contest', NOW()),
('查看比赛详情', '查看比赛详细信息', 'contest:view', 'contest', NOW()),
('更新比赛', '修改比赛', 'contest:update', 'contest', NOW()),
('删除比赛', '删除比赛', 'contest:delete', 'contest', NOW()),
('分配比赛题目', '为比赛分配题目', 'contest:assign-problem', 'contest', NOW()),
('查看比赛题目', '查看比赛的题目列表', 'contest:view-problem', 'contest', NOW()),
('移除比赛题目', '移除比赛的题目', 'contest:remove-problem', 'contest', NOW());

-- 提交记录权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建提交', '提交代码', 'submission:create', 'submission', NOW()),
('查看提交列表', '获取提交记录列表', 'submission:list', 'submission', NOW()),
('查看提交详情', '查看提交记录详细信息', 'submission:view', 'submission', NOW()),
('更新提交状态', '更新提交记录状态', 'submission:update', 'submission', NOW()),
('删除提交', '删除提交记录', 'submission:delete', 'submission', NOW());

-- 公告管理权限
INSERT INTO `permission` (`permission_name`, `description`, `permission_code`, `module`, `created_at`) VALUES
('创建公告', '新增公告', 'announcement:create', 'announcement', NOW()),
('查看公告列表', '获取公告列表', 'announcement:list', 'announcement', NOW()),
('查看公告详情', '查看公告详细信息', 'announcement:view', 'announcement', NOW()),
('更新公告', '修改公告', 'announcement:update', 'announcement', NOW()),
('删除公告', '删除公告', 'announcement:delete', 'announcement', NOW());

-- 3. 为管理员分配所有权限（假设角色ID=1）
INSERT INTO `role_permission` (`role_id`, `permission_id`, `created_at`)
SELECT 1, id, NOW() FROM `permission`;

-- 4. 为学生分配相应权限（假设角色ID=2）
INSERT INTO `role_permission` (`role_id`, `permission_id`, `created_at`)
SELECT 2, id, NOW() FROM `permission` 
WHERE `permission_code` IN (
    'problem:list', 'problem:view',
    'contest:list', 'contest:view', 'contest:view-problem',
    'submission:create', 'submission:list', 'submission:view',
    'announcement:list', 'announcement:view'
);

-- 7. 创建默认管理员账号（密码: admin123，使用BCrypt加密）
-- BCrypt hash for 'admin123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO `user` (`username`, `password`, `email`, `real_name`, `status`, `last_login_time`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@cqie.edu.cn', '系统管理员', 1, NOW(), NOW(), NOW()),
('student', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student@cqie.edu.cn', '测试学生', 1, NOW(), NOW(), NOW());

-- 8. 将管理员账号分配管理员角色（假设用户ID=1，角色ID=1）
INSERT INTO `user_role` (`user_id`, `role_id`, `created_at`) VALUES (1, 1, NOW());

-- 9. 将学生账号分配学生角色（假设用户ID=2，角色ID=2）
INSERT INTO `user_role` (`user_id`, `role_id`, `created_at`) VALUES (2, 2, NOW());

-- =============================================
-- 说明：
-- 1. 默认账号信息：
--    - 管理员：admin / admin123 （拥有所有权限）
--    - 学生：student / admin123 （仅可查看题目、比赛、提交代码）
-- 2. 权限代码格式：模块:操作，如 user:create
-- 3. 支持AND/OR逻辑关系进行权限组合
-- 4. 所有接口默认需要登录，除非明确标注 requireLogin = false
-- 5. 角色说明：
--    - 管理员(ID=1): 拥有所有权限，可管理用户、题目、比赛、公告等
--    - 学生(ID=2): 可查看题目和比赛、提交代码、查看自己的提交记录
-- =============================================

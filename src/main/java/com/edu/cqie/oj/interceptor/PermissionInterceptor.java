package com.edu.cqie.oj.interceptor;

import com.edu.cqie.oj.annotation.RequiresPermission;
import com.edu.cqie.oj.context.UserContext;
import com.edu.cqie.oj.domain.Result;
import com.edu.cqie.oj.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.edu.cqie.oj.service.IUserService;
import com.edu.cqie.oj.mapper.UserRoleMapper;
import com.edu.cqie.oj.mapper.RolePermissionMapper;
import com.edu.cqie.oj.mapper.PermissionMapper;
import com.edu.cqie.oj.domain.entity.UserRoleEntity;
import com.edu.cqie.oj.domain.entity.RolePermissionEntity;
import com.edu.cqie.oj.domain.entity.PermissionEntity;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.edu.cqie.oj.domain.entity.table.UserRoleEntityTableDef.USER_ROLE_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.RolePermissionEntityTableDef.ROLE_PERMISSION_ENTITY;
import static com.edu.cqie.oj.domain.entity.table.PermissionEntityTableDef.PERMISSION_ENTITY;

/**
 * 权限拦截器
 * 实现方法级别的权限控制
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Resource
    private UserRoleMapper userRoleMapper;
    
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    
    @Resource
    private PermissionMapper permissionMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 获取方法上的权限注解
        RequiresPermission permissionAnnotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        
        // 如果方法上没有注解，检查类上是否有注解
        if (permissionAnnotation == null) {
            permissionAnnotation = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
        }
        
        // 如果没有权限注解，直接放行
        if (permissionAnnotation == null) {
            return true;
        }
        
        // 检查是否需要登录
        if (permissionAnnotation.requireLogin()) {
            String token = request.getHeader("Authorization");
            
            if (token == null || token.isEmpty()) {
                sendErrorResponse(response, 401, "未登录，请先登录");
                return false;
            }
            
            // 去除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            try {
                // 验证Token并获取用户信息
                Integer userId = JwtUtil.getUserIdFromToken(token);
                String username = JwtUtil.getUsernameFromToken(token);
                
                // 获取用户的所有权限
                List<String> permissions = getUserPermissions(userId);
                
                // 设置用户上下文
                UserContext.setCurrentUser(new UserContext.UserInfo(userId, username, permissions));
                
            } catch (Exception e) {
                sendErrorResponse(response, 401, "Token无效或已过期，请重新登录");
                return false;
            }
        }
        
        // 获取注解中配置的权限
        String[] requiredPermissions = permissionAnnotation.value();
        
        // 如果没有配置权限代码，只需要登录即可
        if (requiredPermissions.length == 0) {
            return true;
        }
        
        // 获取当前用户的权限列表
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            sendErrorResponse(response, 403, "无权访问，请先登录");
            return false;
        }
        
        // 根据逻辑关系判断权限
        boolean hasPermission;
        if (permissionAnnotation.logical() == RequiresPermission.Logical.AND) {
            // AND逻辑：需要拥有所有权限
            hasPermission = true;
            for (String permission : requiredPermissions) {
                if (!currentUser.hasPermission(permission)) {
                    hasPermission = false;
                    break;
                }
            }
        } else {
            // OR逻辑：拥有任一权限即可
            hasPermission = false;
            for (String permission : requiredPermissions) {
                if (currentUser.hasPermission(permission)) {
                    hasPermission = true;
                    break;
                }
            }
        }
        
        if (!hasPermission) {
            sendErrorResponse(response, 403, "权限不足，无法访问该资源");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除用户上下文
        UserContext.clear();
    }
    
    /**
     * 获取用户的所有权限代码
     *
     * @param userId 用户ID
     * @return 权限代码列表
     */
    private List<String> getUserPermissions(Integer userId) {
        // 查询用户的所有角色
        QueryWrapper userRoleQuery = QueryWrapper.create()
                .from(USER_ROLE_ENTITY)
                .where(USER_ROLE_ENTITY.USER_ID.eq(userId));
        List<UserRoleEntity> userRoles = userRoleMapper.selectListByQuery(userRoleQuery);
        
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有角色ID
        List<Integer> roleIds = userRoles.stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());
        
        // 查询这些角色的所有权限
        QueryWrapper rolePermissionQuery = QueryWrapper.create()
                .from(ROLE_PERMISSION_ENTITY)
                .where(ROLE_PERMISSION_ENTITY.ROLE_ID.in(roleIds));
        List<RolePermissionEntity> rolePermissions = rolePermissionMapper.selectListByQuery(rolePermissionQuery);
        
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有权限ID
        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermissionEntity::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询权限详情
        QueryWrapper permissionQuery = QueryWrapper.create()
                .from(PERMISSION_ENTITY)
                .where(PERMISSION_ENTITY.ID.in(permissionIds));
        List<PermissionEntity> permissions = permissionMapper.selectListByQuery(permissionQuery);
        
        // 返回权限代码列表
        return permissions.stream()
                .map(PermissionEntity::getPermissionCode)
                .collect(Collectors.toList());
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

package com.edu.cqie.oj.context;

import java.util.List;

/**
 * 用户上下文持有类
 * 用于在请求处理过程中存储当前用户信息
 */
public class UserContext {
    
    private static final ThreadLocal<UserInfo> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();
    
    /**
     * 用户信息内部类
     */
    public static class UserInfo {
        private Integer userId;
        private String username;
        private List<String> permissions;
        
        public UserInfo(Integer userId, String username, List<String> permissions) {
            this.userId = userId;
            this.username = username;
            this.permissions = permissions;
        }
        
        public Integer getUserId() {
            return userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public List<String> getPermissions() {
            return permissions;
        }
        
        public boolean hasPermission(String permission) {
            return permissions != null && permissions.contains(permission);
        }
    }
    
    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(UserInfo userInfo) {
        USER_INFO_THREAD_LOCAL.set(userInfo);
    }
    
    /**
     * 获取当前用户信息
     */
    public static UserInfo getCurrentUser() {
        return USER_INFO_THREAD_LOCAL.get();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Integer getCurrentUserId() {
        UserInfo userInfo = USER_INFO_THREAD_LOCAL.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        UserInfo userInfo = USER_INFO_THREAD_LOCAL.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }
    
    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        UserInfo userInfo = USER_INFO_THREAD_LOCAL.get();
        return userInfo != null && userInfo.hasPermission(permission);
    }
    
    /**
     * 清除当前用户信息
     */
    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}

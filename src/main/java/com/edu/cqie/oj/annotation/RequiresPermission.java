package com.edu.cqie.oj.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 用于方法级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 权限代码，如 "user:create", "role:delete"
     * 支持多个权限，满足其中一个即可访问
     */
    String[] value() default {};
    
    /**
     * 是否需要登录，默认true
     */
    boolean requireLogin() default true;
    
    /**
     * 逻辑关系：AND-需要所有权限，OR-满足任一权限即可
     */
    Logical logical() default Logical.OR;
    
    enum Logical {
        AND, OR
    }
}

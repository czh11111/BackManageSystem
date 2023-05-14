package com.qingge.springboot.config;

import java.lang.annotation.*;


/**
 *自定义注解
 * 添加该注解的handler不需要权限认证
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {

}

package com.company.rpcspringbootstarter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标识服务，注入远程服务
 *
 * @author wei.song
 * @since 2023/1/18 14:23
 */
@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceReference {

    String value() default "";

}

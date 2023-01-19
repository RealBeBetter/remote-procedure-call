package com.company.rpcspringbootstarter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标识服务提供者，暴露服务接口
 *
 * @author wei.song
 * @since 2023/1/19 09:54
 */
@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceExpose {

    String value() default "";

}

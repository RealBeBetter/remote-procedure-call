package com.company.rpcspringbootstarter.annotation;

import java.lang.annotation.*;

/**
 * 标识服务，注入远程服务
 *
 * @author wei.song
 * @since 2023/1/18 14:23
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceReference {

    String value() default "";

}

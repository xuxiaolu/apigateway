package com.xuxl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParameter {
    /**
     * 是否为必要参数
     */
    boolean required();


    /**
     * 参数名称
     */
    String name();

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 参数注释
     */
    String desc();
}


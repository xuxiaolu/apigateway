package com.xuxl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiGroup {

    /**
     * ApiGroup名称
     *
     * @return
     */
    String name();


    /**
     * ApiGroup负责人
     *
     * @return
     */
    String owner() default "";
}

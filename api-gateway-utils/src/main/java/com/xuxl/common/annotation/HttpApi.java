package com.xuxl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {
    /**
     * Http 接口名
     *
     * @return
     */
    String name();

    /**
     * Http 接口注释
     *
     * @return
     */
    String desc();


    /**
     * 接口负责人
     *
     * @return
     */
    String owner() default "";
    
    /**
     * 请求方式,get,post
     * @return
     */
    String type();

}

package com.zeroq6.common.datasource;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用自定义transactionManager（事务）和myBatis拦截器的方式（非事务）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated
public @interface DataSource {

    EmDataSourceType value() default EmDataSourceType.MASTER;

}

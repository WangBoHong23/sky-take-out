package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能描述
 *自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 * 填充修改时间及操作人
 * @Description TODO
 * @ClassName AutoFill
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //其中OperationType已在sky-common模块中定义
    OperationType value();//定义操作的类型
}

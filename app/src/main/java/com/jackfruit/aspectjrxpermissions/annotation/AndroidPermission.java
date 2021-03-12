package com.jackfruit.aspectjrxpermissions.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 猿小蔡
 * @name AspectJDemo
 * @class name：com.jackfruit.aspectjdemo.annotation
 * @class describe
 * @createTime 2021/3/11 17:36
 * @change
 * @changTime
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AndroidPermission {

  /**
   * 请求权限
   */
  String[] permissions();

}

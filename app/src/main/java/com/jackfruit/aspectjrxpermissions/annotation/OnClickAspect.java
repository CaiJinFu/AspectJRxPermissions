package com.jackfruit.aspectjrxpermissions.annotation;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author 猿小蔡
 * @name AspectJDemo
 * @class name：com.jackfruit.aspectjdemo.annotation
 * @class describe
 * @createTime 2021/3/11 15:33
 * @change
 * @changTime
 */
@Aspect
public class OnClickAspect {

  //@Pointcut声明切入点
  //切入点是MainActivity的getTime方法，参数不限，前面的*代表返回值不限
  // @Pointcut("execution(* com.jackfruit.aspectjdemo.MainActivity.getTime(..))")
  //第一个星表示返回值，第二个星表示方法
  //表示被ExecuteTime注解的任性方法返回的任性类型，方法里的参数也是任意
  @Pointcut("execution(@ExecuteTime * * (..))")
  public void getTime() {

  }

  @Around("getTime()")
  public void handleGetTime(ProceedingJoinPoint point) {
    long startTime = System.currentTimeMillis();
    try {
      point.proceed();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    long endTime = System.currentTimeMillis();
    Log.i("TAG", "handleGetTime: " + (endTime - startTime));
  }
}

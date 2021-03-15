package com.jackfruit.aspectjrxpermissions.annotation;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.jackfruit.aspectjrxpermissions.rxpermissions2.Permission;
import com.jackfruit.aspectjrxpermissions.rxpermissions2.RxPermissions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import io.reactivex.functions.Consumer;

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
public class AndroidPermissionAspect {

  @Pointcut("execution(@AndroidPermission * * (..))")
  public void permission() {

  }

  @SuppressLint("CheckResult")
  @Around("permission()")
  public void handlePermission(ProceedingJoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    if (!method.isAnnotationPresent(AndroidPermission.class)) {
      return;
    }
    AndroidPermission permission = method.getAnnotation(AndroidPermission.class);
    String[] permissions = permission.permissions();
    if (permissions.length <= 0) {
      try {
        joinPoint.proceed();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return;
    }
    Object target = joinPoint.getThis();
    RxPermissions rxPermission;
    if (target instanceof Fragment) {
      Fragment fragment = (Fragment) target;
      rxPermission = new RxPermissions(fragment);
    } else if (target instanceof FragmentActivity) {
      FragmentActivity activity = (FragmentActivity) target;
      rxPermission = new RxPermissions(activity);
    } else {
      try {
        joinPoint.proceed();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return;
    }
    rxPermission.requestEachCombined(permissions)
        .subscribe(new Consumer< Permission >() {
          @Override
          public void accept(Permission permission) throws Exception {
            Consumer< Permission > onNext = null;
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
              for (Object arg : args) {
                if (arg instanceof Consumer) {
                  onNext = (Consumer< Permission >) arg;
                }
              }
            }
            if (onNext != null) {
              onNext.accept(permission);
            } else {
              if (permission.granted) {
                // 用户已经同意该权限
                try {
                  joinPoint.proceed();
                } catch (Throwable throwable) {
                  throwable.printStackTrace();
                }
              } else if (permission.shouldShowRequestPermissionRationale) {
                Log.i("TAG", "拒绝权限: ");
              } else {
                Log.i("TAG", "点击不再询问权限: ");
              }
            }
          }
        });
  }
}

# AspectJRxPermissions

[TOC]



## AOP

AOP为Aspect Oriented Programming的缩写，翻译：面向切面编程。它是通过预编译方式和运行期间动态代理来实现程序功能的统一维护的一种技术。

AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。

## AOP应用场景

- 权限校验
- 日志上传
- 行为统计
- 性能监测

## AspectJ是什么？

AspectJ是一个面向切面的框架，它扩展了Java语言。AspectJ定义了AOP语法，它有一个专门的编译器用来生成遵守Java字节编码规范的Class文件。

## AspectJ怎样替我们实现AOP？

在程序编译过程中通过编译时技术将字节码文件中织入我们自己定义的切面。代码。注意：不管使用哪种方式接入AspectJ，都需要使用AspectJ提供的代码编译工具ajc进行编译。

## 使用

### 添加依赖

在工程build.gradle目录下

```groovy
dependencies {
  classpath "com.android.tools.build:gradle:4.1.2"
  classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
  // NOTE: Do not place your application dependencies here; they belong
  // in the individual module build.gradle files
}
```



在APPbuild.gradle目录下

```groovy
plugins {
  id 'com.android.application'
  id 'android-aspectjx'
}
```



```groovy
dependencies {

  implementation 'androidx.appcompat:appcompat:1.2.0'
  implementation 'com.google.android.material:material:1.2.1'
  implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
  testImplementation 'junit:junit:4.13.2'
  androidTestImplementation 'androidx.test.ext:junit:1.1.2'
  androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
  //AOP
  implementation 'org.aspectj:aspectjrt:1.9.6'
}
```



### 语法

**关键字**

call：调用切入点的方法。

execution：得到这个切入点的方法

*：代表任意对象 任意字符

通过类方法

```java
public void getTime() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Log.i("TAG", "执行了getTime方法: ");
  }
```



```java
@Aspect
public class OnClickAspect {

  //@Pointcut声明切入点
  //切入点是MainActivity的getTime方法，参数不限，前面的*代表返回值不限
  @Pointcut("execution(* com.jackfruit.aspectjdemo.MainActivity.getTime(..))")
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
```

一定要先Rebuild Project一下，不然会报错。

通过注解的方式添加AspectJ

定义注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ExecuteTime {
}
```

切点换成注解

```java
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
```

添加注解在方法上

```java
@ExecuteTime
  public void getTime() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Log.i("TAG", "执行了getTime方法: ");
  }
```



## Android权限申请

通过aspectJ与RxJava动态申请Android权限，申请权限的代码是RxPermissions库，在此之上我加上了aspectJ。

申请权限的代码就不说了，具体可看我的GitHub上的Demo，链接：[https://github.com/CaiJinFu/AspectJRxPermissions](https://github.com/CaiJinFu/AspectJRxPermissions)。

定义权限的注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AndroidPermission {

  /**
   * 请求权限
   */
  String[] permissions();

}
```

出来注解的aspectJ类

```java
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
        });
  }
}
```

调用

```java
@AndroidPermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE})
public void getPermission() {
  Log.i("TAG", "获取权限成功了: ");
}
```

注意事项：

```java
//定义此注解的方法，一定要定义在activity或者fragment内的方法，不要定义在内部类中(包括匿名内部类中)。
//在被@Aspect的类中，不要使用lambd表达式，否则会报错，编译不通过。
```

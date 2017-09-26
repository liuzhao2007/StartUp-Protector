# StartUp-Protector

**避免App无法启动以及多次启动崩溃的框架，启动保护的最后防线。**

# 一、功能特性
1. 崩溃检测及分级策略：两次崩溃执行一级安全模式，三次崩溃执行二级安全模式；
2. 提供自修复能力，可自定义进入安全模式的处理策略；
3. 提供阻塞进程能力，可执行同步热修复；
4. 提供详细崩溃信息的获取及崩溃的回调能力；
5. 可定制崩溃后策略，例如重启的忽略策略；
6. 提供快速回归的能力；


# 二、应用场景
**主要针对应用启动阶段崩溃，例如接口错误、文件损坏等，可能会发生连续Crash；而即便是有热修复，但Crash发生在热修复之前也是无能为力。因此我们需要一道防线，可以对App进行主动的自修复，同时提供同步的热修复能力；那么任何启动崩溃就都不怕了。**

# 三、集成使用
- Gradle中配置；

```
    compile ''
```

- 初始化；
    - Application中设置；
        ```
            Protector.getInstance()
                    .addTask(new Runnable() {// register Runnable to be executed when firstlevel crash occured.
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "执行注册逻辑", 1).show();
                        }
                    })
                    .addSynchronousTask(new TestProtectorTask())
                    .addCrashManager(new TestCrashManager())
                    .setCrashCallBack(new TestCrashCallBack())
                    .setRestart(false)
                    .init(ProtectorApp.this);
        ```
    - 退出应用的时候调用，崩溃的统计会更加精确；
        ```
            Protector.getInstance().lanuchSucceed();
        ```

# 四、详细说明

- addTask();    注册行为在一级安全模式触发；
- addSynchronousTask();     注册阻塞的行为在二级安全模式触发，例如热修复等；
- addCrashManager();    Crash的管理类，决定是否重启，例如场景重启；
- setCrashCallBack();   Crash的回调，可获取到崩溃的信息，有原始的Throwable以及整理后的Msg；
- setRestart();     设置是否重启，默认为true；true的状态下根据CrashManager最终决定是否重启，false则一定不会重启；
- setDebug();   Log的开关；

# 五、其它

### 1、问题；
使用过程可根据Log输出信息观察，tag为protector；

### 2、测试；
```
    ProtectorTest.testJavaCrash();// 调用之后，应用就会崩溃；
```

### 3、混淆；
如果使用了Proguard，则需要添加以下混淆规则；

```
    -keep public class com.android.startup.protector.**{*;}
    -keep class * implements com.android.startup.protector.iprotector.CrashCallBack{*;}
    -keep class * implements com.android.startup.protector.iprotector.CrashManager{*;}
    -keep class * extends com.android.startup.protector.iprotector.ProtectorTask{*;}
```

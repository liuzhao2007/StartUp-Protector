package com.android.startup.protectordemo;

import com.android.startup.protector.util.ProtectorLogUtils;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class TestCrashHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        ProtectorLogUtils.i("crash occur");
    }
}

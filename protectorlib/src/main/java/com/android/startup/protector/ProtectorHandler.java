package com.android.startup.protector;

/**
 * Created by liuzhao on 2017/9/22.
 */

public class ProtectorHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public ProtectorHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.mDefaultUncaughtExceptionHandler = exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {


        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        }


    }

}

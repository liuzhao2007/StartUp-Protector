package com.android.startup.protector.iprotector;

/**
 * Created by liuzhao on 2017/9/26.
 */

public interface CrashCallBack {
    void uncaughtException(Throwable ex,String crashMsg);
}

package com.android.startup.protector.iprotector;

/**
 * Created by liuzhao on 2017/9/26.
 * <p>
 * give the original Throwable and the CrashMsg to users when a crash happened
 */
public interface CrashCallBack {
    void uncaughtException(Throwable ex, String crashMsg);
}

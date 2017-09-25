package com.android.startup.protector.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by liuzhao on 2017/9/25.
 */

public class ProtectorUtils {
    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context == null) {
            return "";
        }
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        if (mActivityManager != null && runningAppProcessInfos != null && runningAppProcessInfos.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfos) {
                if (appProcess != null && appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }
}

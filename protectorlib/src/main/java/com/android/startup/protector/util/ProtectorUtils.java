package com.android.startup.protector.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by liuzhao on 2017/9/25.
 */

public class ProtectorUtils {
    /**
     * getCurrentProcessName
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


    /**
     * is main process
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        try {
            ActivityManager am = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningAppProcessInfo> processInfo = am.getRunningAppProcesses();
            String mainProcessName = context.getPackageName();
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfo) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ReStart MySelf
     *
     * @param context
     * @param packName
     */
    public static void restartApp(Context context) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packInfo.activities;
            if (activities != null && activities.length != 0) {
                ActivityInfo startActivity = activities[0];
                Intent intent = new Intent();
                intent.setClassName(context.getPackageName(), startActivity.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            ProtectorLogUtils.e("Serious Error: restart app failed !!!");
            e.printStackTrace();
        }
    }
}

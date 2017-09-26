package com.android.startup.protector.handler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.startup.protector.Protector;
import com.android.startup.protector.constant.SpConstant;
import com.android.startup.protector.iprotector.CrashManager;
import com.android.startup.protector.util.ProtectorLogUtils;
import com.android.startup.protector.util.ProtectorUtils;
import com.android.startup.protector.util.ProtectorSpUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by liuzhao on 2017/9/22.
 */

public class ProtectorHandler implements Thread.UncaughtExceptionHandler {

    private static final long TIME_CRASHNOTREOPEN = 10000;//the time not to setRestart after crash
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public ProtectorHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.mDefaultUncaughtExceptionHandler = exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        Context context = Protector.getInstance().getContext();
        String crashMsg = getCrashInfo(ex);
        if (Protector.getInstance().getCrashCallBack() != null) {
            Protector.getInstance().getCrashCallBack().uncaughtException(ex,crashMsg);
        }
        String packName = context.getPackageName();
        ProtectorLogUtils.e("CrashMsg:" + crashMsg);
        long crashtime = System.currentTimeMillis();
        long lastCrashTime = ProtectorSpUtils.getLong(SpConstant.CRASHTIME, 0);
        ProtectorLogUtils.e("ThisCrashTime" + crashtime + "————》" + "LastCrashTime:" + lastCrashTime);
        if (crashtime - lastCrashTime > TIME_CRASHNOTREOPEN && Protector.getInstance().restartApp) {
            ProtectorLogUtils.e("more than time we define, may setRestart app");
            boolean ifStart = true;
            List<CrashManager> mUserCrashManagers = Protector.getInstance().getUserCrashManagers();
            // we need to konw if this crash satisfy the Situation to setRestart
            if (mUserCrashManagers != null && !mUserCrashManagers.isEmpty()) {
                for (CrashManager iCrashManager : mUserCrashManagers) {
                    if (!iCrashManager.ifRestart(crashMsg)) {
                        ifStart = false;
                    }
                }
            }
            if (ifStart) {
                ProtectorLogUtils.e("decide to setRestart app");
                ProtectorSpUtils.putLong(SpConstant.CRASHTIME, crashtime);
                restartApp(context, packName);
            }
        }
        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, ex);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void restartApp(Context context, String packName) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    packName,
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packInfo.activities;
            if (activities != null && activities.length != 0) {
                ActivityInfo startActivity = activities[0];
                Intent intent = new Intent();
                intent.setClassName(packName, startActivity.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get info include crash and device to report or record
     *
     * @param ex
     * @return
     * @throws Exception
     */
    public String getCrashInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = Protector.getInstance().getContext().getPackageManager();
        PackageInfo packageInfo = null;
        // get crash info
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        String string = writer.toString();
        try {
            sb.append(string);
            packageInfo = pm.getPackageInfo(Protector.getInstance().getContext().getPackageName(),
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_ACTIVITIES);
            sb.append("VersionCode = " + packageInfo.versionName);
            sb.append("\n");

            // get device info
            Field[] fields = Build.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                String name = fields[i].getName();
                sb.append(name + " = ");
                String value = fields[i].get(null).toString();
                sb.append(value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.append("myTid = " + android.os.Process.myTid()).append("\n")
                .append("myPid = " + android.os.Process.myPid()).append("\n")
                .append("myUid = " + android.os.Process.myUid()).append("\n")
                .append("ThreadName = " + Thread.currentThread().getName()).append("\n")
                .append("ProcessName = " + ProtectorUtils.getCurProcessName(Protector.getInstance().getContext())).toString();
    }

}

package com.android.startup.protector.handler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.startup.protector.Protector;
import com.android.startup.protector.constant.SpConstant;
import com.android.startup.protector.util.LogUtils;
import com.android.startup.protector.util.ProtectorUtils;
import com.android.startup.protector.util.SpUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by liuzhao on 2017/9/22.
 */

public class ProtectorHandler implements Thread.UncaughtExceptionHandler {

    private static final long TIME_CRASHNOTREOPEN = 10000;//crash之后不重启的时间。
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public ProtectorHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.mDefaultUncaughtExceptionHandler = exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        LogUtils.i("uncaughtException");

        Context context = Protector.getInstance().getContext();
        String errorMsg = getCrashInfo(ex);
        String packName = context.getPackageName();
        LogUtils.e("CrashInfo:" + errorMsg);
        long crashtime = System.currentTimeMillis();
        long lastCrashTime = SpUtils.getLong(SpConstant.CRASHTIME, 0);
        LogUtils.e("ThisCrashTime" + crashtime + "————》" + "LastCrashTime:" + lastCrashTime);
        if (crashtime - lastCrashTime > TIME_CRASHNOTREOPEN && Protector.getInstance().restartApp) {
            LogUtils.e("超过十秒");
            // 两次崩溃时间超过限制才去重启。十秒之内的崩溃第二次连续崩溃，不重启。
//                if (CrashManager.ifRestart(errorMsg)) {
            // 根据CrashManager的判断来决定重启与否。
            LogUtils.e("要重启");
            restartApp(context, packName);

            SpUtils.putLong(SpConstant.CRASHTIME, crashtime);
        }

//            android.os.Process.killProcess(android.os.Process.myPid());

        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, ex);
        }
    }

    private void restartApp(Context context, String packName) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    packName,
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packInfo.activities;
            if (activities.length != 0) {
                ActivityInfo startActivity = activities[0];
                Intent intent = new Intent();
                intent.setClassName(packName, startActivity.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Crash的信息；方便定位、后期统计。
     *
     * @param ex
     * @return
     * @throws Exception
     */
    public String getCrashInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = Protector.getInstance().getContext().getPackageManager();
        PackageInfo packageInfo = null;
        // 获取错误的堆栈信息:先获取堆栈信息，然后获取手机内存信息
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

            // 然后获取用户的手机硬件信息
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
                .append("ThreadId = " + Thread.currentThread().getName()).append("\n")
                .append("ProcessName = " + ProtectorUtils.getCurProcessName(Protector.getInstance().getContext())).toString();
    }

}

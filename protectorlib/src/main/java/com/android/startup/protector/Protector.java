package com.android.startup.protector;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.startup.protector.clear.ProtectorClearer;
import com.android.startup.protector.constant.SpConstant;
import com.android.startup.protector.handler.ProtectorHandler;
import com.android.startup.protector.iprotector.ICrashManager;
import com.android.startup.protector.iprotector.ProtectorTask;
import com.android.startup.protector.util.ProtectorLogUtils;
import com.android.startup.protector.util.ProtectorSpUtils;
import com.android.startup.protector.util.ProtectorThreadUtils;
import com.android.startup.protector.util.ProtectorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhao on 2017/9/22.
 */

public class Protector {
    private static Context context;
    private static Protector mProtector;
    private List<Runnable> mUserTasks = new ArrayList<>();// tasks user define
    private List<ICrashManager> mUserCrashManagers = new ArrayList<>();// crashManager user define
    private static final int firstLevel = 3;
    private static final int SecondLevel = 5;
    public boolean restartApp = true;
    private ProtectorTask mProtectorTask;

    private Protector() {
    }

    public static Protector getInstance() {
        if (mProtector == null) {
            synchronized (Protector.class) {
                if (mProtector == null) {
                    mProtector = new Protector();
                }
            }
        }
        return mProtector;
    }

    public void init(Application application) {
        if (application == null) {
            ProtectorLogUtils.e("serious error : param application is null");
            return;
        }

        if (!ProtectorUtils.isMainProcess(application)) {
            return;
        }
        context = application;
        ProtectorSpUtils.putInt(SpConstant.CRASHCONUT, ProtectorSpUtils.getInt(SpConstant.CRASHCONUT, 0) + 1);
        int countNow = ProtectorSpUtils.getInt(SpConstant.CRASHCONUT, 0);
        if (countNow > firstLevel) {
            ProtectorLogUtils.i("enter level one");
            for (Runnable runnable : mUserTasks) {
                if (runnable != null) {
                    ProtectorThreadUtils.getInstance().execute(runnable);
                }
            }
            if (countNow >= SecondLevel) {
                // clear all and fix
                ProtectorLogUtils.i("enter level two");
                ProtectorClearer.clearAllFile(context);

                if (mProtectorTask != null) {
                    // suspend the process , you can do a time-consuming operation for example hotfix here
                    ProtectorThreadUtils.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            mProtectorTask.doInBackground();
                        }
                    });
                    while (!mProtectorTask.isFinished()) {
                        // do nothing here, which can save memory and cpu.
                    }
                }
            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                markSucceed();
            }
        }, 5000);
        Thread.setDefaultUncaughtExceptionHandler(new ProtectorHandler(Thread.getDefaultUncaughtExceptionHandler()));
    }

    public Protector addTask(Runnable runnable) {
        mUserTasks.add(runnable);
        return this;
    }

    public Protector addCrashManager(ICrashManager crashManager) {
        mUserCrashManagers.add(crashManager);
        return this;
    }

    public Protector addSynchronousTask(ProtectorTask protectorTask) {
        mProtectorTask = protectorTask;
        return this;
    }

    // mark as app lanuch successed
    public void markSucceed() {
        ProtectorSpUtils.putInt(SpConstant.CRASHCONUT, 0);
        ProtectorLogUtils.i("markSuceed");
    }

    // if try to restart app
    public void restart(boolean restart) {
        restartApp = restart;
    }

    public Protector setDebug(boolean isDebug) {
        ProtectorLogUtils.setDebug(isDebug);
        ProtectorLogUtils.i("StartUp-Protector debug : " + isDebug);
        return this;
    }

    public Context getContext() {
        return context;
    }

    public List<ICrashManager> getUserCrashManagers() {
        return mUserCrashManagers;
    }

}

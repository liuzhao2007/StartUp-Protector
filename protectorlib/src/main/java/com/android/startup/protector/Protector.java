package com.android.startup.protector;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.startup.protector.clear.ProtectorClearer;
import com.android.startup.protector.constant.SpConstant;
import com.android.startup.protector.handler.ProtectorExceptionHandler;
import com.android.startup.protector.iprotector.CrashCallBack;
import com.android.startup.protector.iprotector.CrashManager;
import com.android.startup.protector.iprotector.ProtectorTask;
import com.android.startup.protector.util.ProtectorLogUtils;
import com.android.startup.protector.util.ProtectorSpUtils;
import com.android.startup.protector.util.ProtectorThreadUtils;
import com.android.startup.protector.util.ProtectorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhao on 2017/9/22.
 * <p>
 * StartUp-Protector 入口类
 */

public class Protector {
    private static Context context;
    private static volatile Protector mProtector;
    private List<Runnable> mUserTasks = new ArrayList<>();// tasks user define
    private List<CrashManager> mUserCrashManagers = new ArrayList<>();// crashManager user define
    private static final int Times_FirstLevel = 2;// 崩溃等级一
    private static final int Times_SecondLevel = 3;// 崩溃等级二
    private static final int Times_WorstLevel = 5;// 崩溃等级三，最严重
    private ProtectorTask mSynProtectorTask;// 阻塞情况下执行的Task
    private boolean mDefaultReStart = true;// 默认是否重启，默认true

    private CrashCallBack mCrashCallBack;

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
            ProtectorLogUtils.e("serious error : param application is null ,Protector init failed !!!");
            return;
        }

        if (!ProtectorUtils.isMainProcess(application)) {
            return;// only for MainProcess, else just return
        }
        context = application;
        ProtectorSpUtils.putInt(SpConstant.CRASHCONUT, ProtectorSpUtils.getInt(SpConstant.CRASHCONUT, 0) + 1);

        Thread.setDefaultUncaughtExceptionHandler(new ProtectorExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()));

        int countNow = ProtectorSpUtils.getInt(SpConstant.CRASHCONUT, 0);
        if (countNow > Times_FirstLevel) {
            ProtectorLogUtils.i("enter level one");
            for (Runnable runnable : mUserTasks) {
                if (runnable != null) {
                    ProtectorThreadUtils.getInstance().execute(runnable);
                }
            }

            if (countNow > Times_SecondLevel) {
                // clear all
                ProtectorLogUtils.i("enter level two");
                ProtectorClearer.clearAllFile(context);

                if (countNow >= Times_WorstLevel && mSynProtectorTask != null) {
                    // fix operation can be done here
                    ProtectorLogUtils.i("enter level three ,worst");
                    // suspend the process , you can do a time-consuming operation for example hotfix here
                    ProtectorThreadUtils.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            mSynProtectorTask.doInBackground();
                        }
                    });
                    while (!mSynProtectorTask.isFinished()) {
                        // do nothing here, which can save memory and cpu.
                    }
                }
            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // if this is called, we can mark a successful lanuch.
                markLanuchSucceed();
            }
        }, 10000);
    }

    /**
     * add normal task,which will be runnned in FirstLevel situation;
     * can add more than once
     *
     * @param runnable
     * @return
     */
    public Protector addNormalTask(Runnable runnable) {
        mUserTasks.add(runnable);
        return this;
    }

    /**
     * add CrashManager to decide whether to restart
     * you can add more than once
     *
     * @param crashManager
     * @return
     */
    public Protector addCrashManager(CrashManager crashManager) {
        mUserCrashManagers.add(crashManager);
        return this;
    }

    /**
     * add Synchronous task,which will be runned in worst situation;
     * can add more
     *
     * @param synchronousTask
     * @return
     */
    public Protector addSynchronousTask(ProtectorTask synchronousTask) {
        mSynProtectorTask = synchronousTask;
        return this;
    }

    // mark as app lanuch successed
    public void markLanuchSucceed() {
        ProtectorSpUtils.putInt(SpConstant.CRASHCONUT, 0);
        ProtectorLogUtils.i("markSuceed");
    }

    // setCrashCallback to handle crash for example record or report
    public Protector setCrashCallBack(CrashCallBack crashCallBack) {
        mCrashCallBack = crashCallBack;
        return this;
    }

    public Protector setDebug(boolean isDebug) {
        ProtectorLogUtils.setDebug(isDebug);
        ProtectorLogUtils.i("StartUp-Protector Mode : " + (isDebug ? "Debug" : "Release"));
        return this;
    }

    public Context getContext() {
        return context;
    }

    public List<CrashManager> getUserCrashManagers() {
        return mUserCrashManagers;
    }

    public CrashCallBack getCrashCallBack() {
        return mCrashCallBack;
    }

    public boolean isReStart() {
        return mDefaultReStart;
    }

    public void setRestart(boolean defaultReStart) {
        this.mDefaultReStart = defaultReStart;
    }
}

package com.android.startup.protectordemo;

import com.android.startup.protector.iprotector.CrashCallBack;
import com.android.startup.protector.util.ProtectorLogUtils;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class TestCrashCallBack implements CrashCallBack {

    @Override
    public void uncaughtException(Throwable ex, String crashMsg) {
        ProtectorLogUtils.i("crashMsg:" + crashMsg);
        ex.printStackTrace();
    }
}

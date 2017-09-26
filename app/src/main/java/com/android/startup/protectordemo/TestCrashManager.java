package com.android.startup.protectordemo;

import com.android.startup.protector.iprotector.CrashManager;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class TestCrashManager implements CrashManager {

    @Override
    public boolean ifRestart(String crashMsg) {
        // you can deside if setRestart app here ,true to start and false not.
        return true;
    }
}

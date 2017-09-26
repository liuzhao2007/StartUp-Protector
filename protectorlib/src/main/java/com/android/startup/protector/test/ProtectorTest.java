package com.android.startup.protector.test;

import com.android.startup.protector.util.ProtectorLogUtils;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class ProtectorTest {

    public static void testJavaCrash() {
        String string = null;
        ProtectorLogUtils.i("string:" + string);
    }

}

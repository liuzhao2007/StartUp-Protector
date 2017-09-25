package com.android.startup.protectordemo;

import android.app.Application;
import android.widget.Toast;

import com.android.startup.protector.Protector;
import com.android.startup.protector.util.ProtectorLogUtils;

/**
 * Created by liuzhao on 2017/9/25.
 */

public class ProtectorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Protector.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "执行注册逻辑", 1).show();
            }
        }).init(ProtectorApp.this);
//        while (true) {
//            ProtectorLogUtils.i("等待ing");
//        }
    }
}

package com.android.startup.protectordemo;

import com.android.startup.protector.iprotector.ProtectorTask;
import com.android.startup.protector.util.ProtectorLogUtils;
import com.android.startup.protector.util.ProtectorThreadUtils;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class TestProtectorTask extends ProtectorTask {

    @Override
    public void doInBackground() {
        ProtectorThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ProtectorLogUtils.i("I'm trying to hotfix");
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ProtectorLogUtils.i("hotfix end");
                onPostExecute();// this must be called when it's ok
            }
        });
    }

}

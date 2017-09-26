package com.android.startup.protector.iprotector;

import com.android.startup.protector.util.ProtectorThreadUtils;

/**
 * Created by liuzhao on 2017/9/25.
 */

public abstract class ProtectorTask implements Runnable {

    private boolean isFinished = false;

    @Override
    public void run() {
        ProtectorThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                doInBackground();
            }
        });
    }

    public void onPostExecute() {
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public abstract void doInBackground();
}

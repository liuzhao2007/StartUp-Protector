package com.android.startup.protector.iprotector;

/**
 * Created by liuzhao on 2017/9/26.
 * <p>
 * A Chance for caller to decide if restart
 */

public interface CrashManager {

    /**
     * you can decide on the basis of the situation
     *
     * @param crashMsg
     * @return
     */
    boolean ifRestart(String crashMsg);

}

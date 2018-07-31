package com.android.startup.protector.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuzhao on 2017/9/26.
 * <p>
 * the thread count is copied from android/os/AsyncTask.java
 */

public class ProtectorThreadUtils {

    private static volatile ProtectorThreadUtils sProtectorThreadUtils;
    private static ThreadPoolExecutor sExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 10;

    private ProtectorThreadUtils() {
    }

    public static ProtectorThreadUtils getInstance() {
        if (sProtectorThreadUtils == null) {
            synchronized (ProtectorThreadUtils.class) {
                if (sProtectorThreadUtils == null) {
                    sProtectorThreadUtils = new ProtectorThreadUtils();
                    sExecutor = new ThreadPoolExecutor(
                            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        return sProtectorThreadUtils;
    }

    public void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}

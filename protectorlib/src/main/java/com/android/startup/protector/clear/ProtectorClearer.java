package com.android.startup.protector.clear;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by liuzhao on 2017/9/22.
 */

public class ProtectorClearer {

    public static void deleteFile(File file) {
        deleteFile(file, true);
    }

    /**
     * @param file
     * @param isDirRemovable if remove the filefolder
     */
    public static void deleteFile(File file, boolean isDirRemovable) {
        if (file == null || !file.exists()) {
            return;
        }
        file.setWritable(true);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File f : listFiles) {
                    deleteFile(f);
                }
            }
            if (isDirRemovable) {
                file.delete();
            }
        }
    }

    public static void clearAllFile(Context context, String... filePaths) {
        deleteFile(context.getCacheDir());
        deleteFile(context.getFilesDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFile(context.getExternalCacheDir());
        }
        deleteFile(new File("/data/data/" + context.getPackageName() + "/databases"));
        deleteFile(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
        for (String path : filePaths) {
            deleteFile(new File(path));
        }
    }

}

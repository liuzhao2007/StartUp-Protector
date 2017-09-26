package com.android.startup.protector.impl;

import android.text.TextUtils;

import com.android.startup.protector.iprotector.ICrashManager;

import java.util.ArrayList;

/**
 * Created by liuzhao on 2017/9/26.
 */

public class TagCrashManager implements ICrashManager {

    public ArrayList<String> crashTags = new ArrayList<String>();

    public void addCrashTags(ArrayList<String> tags) {
        crashTags.addAll(tags);
    }

    @Override
    public boolean ifRestart(String crashMsg) {
        boolean ifRestart = true;
        if (crashTags == null || crashTags.isEmpty() || TextUtils.isEmpty(crashMsg)) {
            ifRestart = false;
            return ifRestart;
        }
        for (String tag : crashTags) {
            if (!TextUtils.isEmpty(tag) && crashMsg.contains(tag)) {
                ifRestart = false;
            }
        }
        return ifRestart;
    }
}

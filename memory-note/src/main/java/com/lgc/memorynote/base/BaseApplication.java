package com.lgc.memorynote.base;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/05/19
 *      version : 1.0
 * <pre>
 */

public class BaseApplication extends Application{
    final static String TAG = "MyApplication";
    public static BaseApplication appContext;

    public BaseApplication() {
        Log.e(TAG, "MyApplication: 应用创建了");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
}

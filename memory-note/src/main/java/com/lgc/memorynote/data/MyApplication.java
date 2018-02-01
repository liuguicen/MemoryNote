package com.lgc.memorynote.data;

import android.app.Application;
import android.util.Log;

/**
 * 在mainifest中使用android:name=".MyApplication"，系统将会创建myapplication替代一般的application
 */
public class MyApplication extends Application {
    final static String TAG = "MyApplication";
    public static MyApplication appContext;

    public MyApplication() {
        Log.e(TAG, "MyApplication: 应用创建了");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }


}

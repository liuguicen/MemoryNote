package com.lgc.memorynote.base;

import android.app.Application;
import android.util.Log;

/**
 * 在mainifest中使用android:name=".WordListApplication"，系统将会创建myapplication替代一般的application
 */
public class WordListApplication extends Application {
    final static String TAG="WordListApplication";
    public static WordListApplication appContext;
    public WordListApplication(){
        Log.e(TAG, "WordListApplication: 应用创建了");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }


}

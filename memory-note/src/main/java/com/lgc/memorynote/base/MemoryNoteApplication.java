package com.lgc.memorynote.base;

import android.app.Application;
import android.util.Log;

/**
 * 在mainifest中使用android:name=".MemoryNoteApplication"，系统将会创建myapplication替代一般的application
 */
public class MemoryNoteApplication extends Application {
    final static String TAG="MemoryNoteApplication";
    public static MemoryNoteApplication appContext;
    public MemoryNoteApplication(){
        Log.e(TAG, "MemoryNoteApplication: 应用创建了");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logcat.setTag("Memory Note");
        appContext = this;
    }
}

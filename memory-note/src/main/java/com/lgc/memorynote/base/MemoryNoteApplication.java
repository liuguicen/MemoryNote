package com.lgc.memorynote.base;

import android.app.Application;
import android.util.Log;

import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.secretCode.ServiceIdentify;

import cn.bmob.v3.Bmob;

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
        appContext = this;
        Logcat.setTag("Memory Note");
        GlobalData.init();
    }
}

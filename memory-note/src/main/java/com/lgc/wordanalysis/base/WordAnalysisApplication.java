package com.lgc.wordanalysis.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;


import com.lgc.baselibrary.baseComponent.BaseApplication;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.data.GlobalData;

import cn.bmob.v3.Bmob;

/**
 * 在mainifest中使用android:name=".WordAnalysisApplication"，系统将会创建myapplication替代一般的application
 */
public class WordAnalysisApplication extends BaseApplication {
    final static String TAG = "WordAnalysisApplication";
    public static String DEFAULT_FIL_PATH;

    public WordAnalysisApplication() {
        Log.e(TAG, "WordAnalysisApplication: 应用创建了");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logcat.setTag("MemoryNote");
        DEFAULT_FIL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                this.getResources().getString(R.string.app_name);
        Bmob.initialize(this, "63ab0dfdd965aa92efbfce03fd10d082");//再是网络初始化
        GlobalData.init();
    }

    /**
     * 启动后台服务，
     *
     * @see AppIntentService
     * <p>1.在后台发送用户使用信息
     */
    public static void startBackgroundService(Activity activity) {
        Intent intent = new Intent("initDate");
        intent.setAction("com.lgc.memorynote.base.AppIntentService");
        intent.setPackage(activity.getPackageName());
        activity.startService(intent);
    }
}

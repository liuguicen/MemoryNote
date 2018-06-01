package com.lgc.memorynote.base;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lgc.memorynote.base.log.CrashLog;

/**
 * Created by LiuGuicen on 2016/12/25 0025.
 */

public class AppIntentService extends IntentService {
    final static String TAG = "AppIntentService";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AppIntentService() {
        super("AppIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: 后台服务启动了");
        //发送一下crash信息
        if (CrashLog.hasNew()) {
            new CrashLog().serviceCreate();
        }
    }

}

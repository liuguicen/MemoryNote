package com.lgc.wordanalysis.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import cn.bmob.v3.Bmob;

/**
 * Created by LiuGuicen on 2017/1/18 0018.
 */

public class AppIniter {
    public static void init() {
        permission();//首先得申请出所有权限
        Bmob.initialize(MemoryNoteApplication.appContext, "3000c4af659e92854854c5b10f0824a2");//再是网络初始化

        startBackgroundService();
        Log.e("------------", "init: 应用初始化成功");
    }

    /**
     * 启动后台服务，
     * <p>1.在后台发送用户使用信息
     */
    private static void startBackgroundService() {
        Intent intent = new Intent("initDate");
        intent.setAction("a.baozouptu.common.appInfo.AppIntentService");
        MemoryNoteApplication.appContext.startService(intent);
    }

    private static void permission() {
        //权限请求
        PackageManager pm = MemoryNoteApplication.appContext.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MemoryNoteApplication.appContext.getPackageName()));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }
        }
    }
}

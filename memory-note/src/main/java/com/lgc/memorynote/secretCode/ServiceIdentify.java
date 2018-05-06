package com.lgc.memorynote.secretCode;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/05/06
 *      version : 1.0s
 * <pre>
 */

public class ServiceIdentify {
    public static void serviceInit(Application application) {
        Bmob.initialize(application,"63ab0dfdd965aa92efbfce03fd10d082");//再是网络初始化
    }
}

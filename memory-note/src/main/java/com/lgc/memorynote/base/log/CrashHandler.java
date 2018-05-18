package com.lgc.memorynote.base.log;

import android.os.Process;
import android.util.Log;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultHandler;

    public CrashHandler() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("-----------------", "uncaughtException: 发生了Crash");
        new CrashLog().commit(thread, ex);
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}

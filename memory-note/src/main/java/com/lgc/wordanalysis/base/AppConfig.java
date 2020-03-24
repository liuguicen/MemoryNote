package com.lgc.wordanalysis.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lgc.wordanalysis.R;

/**
 * Created by liuguicen on 2016/8/13.
 * <p> 本App的一些信息，特别升级的时候挺多的信息要记录，因为你不知道是从哪个版本升级过来的
 * <p> app版本，数据库版本,..
 * <p>
 * <p><p>
 * //各个历史版本，别删
 * <p>public final static float APP_VERSION_1 = 1.0f;
 * <p>public final static float APP_VERSION_2 = 1.1f;
 * <p>public final static int DATABASE_VERSION_2 = 2;
 * <p>public final static int DATABASE_VERSION_3=3;
 */
public class AppConfig {
    //各个历史版本，别删
    private static PackageInfo pi;

    static {
        PackageManager pm = WordAnalysisApplication.appContext.getPackageManager();
        try {
            pi = pm.getPackageInfo(WordAnalysisApplication.appContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final static int APP_VERSION_1 = 1;
    public final static int APP_VERSION_2 = 2;
    public final static int APP_VERSION_3 = 3;
    public final static int APP_VERSION_4 = 4;
    public final static String CUR_VERSION_NAME = pi.versionName;
    public final static int CUR_APP_VERSION = pi.versionCode;

    public final static int DATABASE_VERSION_1 = 1;
    public final static int DATABASE_VERSION_2 = 2;
    public final static int DATABASE_VERSION_3 = 3;
    public final static int CUR_DATABASE_VERSION = DATABASE_VERSION_1;

    private SharedPreferences sp;

    public static int getDatabaseVersion() {
        return CUR_DATABASE_VERSION;
    }

    public static int getAppversion() {
        return CUR_APP_VERSION;
    }

    public AppConfig(Context appContext) {
        sp = appContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
    }

    public int readAppVersion() {
        return sp.getInt("app_version", -1);
    }

    public void writeCurAppVersion() {
        sp.edit().putInt("app_version", CUR_APP_VERSION)
                .apply();
    }

    public long readConfig_LastUseData() {
        return sp.getLong("last_used_date", 0);
    }

    public void writeConfig_LastUsedData(long data) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong("last_used_date", data);
        spEditor.apply();
    }

    public void clearOldVersionInfo_1_0() {
        SharedPreferences.Editor spEditor = sp.edit();
        //移除1.0版本的ptu上的配置信息，当时模块划分不清晰，也没考虑到模块会变大，变大之后这里变得复杂难写了
        spEditor.remove("text_rubber");
        spEditor.remove("go_send");
        spEditor.remove("usu_pic_use");
        spEditor.remove("isNewInstall");
        if (!spEditor.commit()) {
            Log.e(WordAnalysisApplication.appContext.getResources().getText(R.string.app_name).toString(), "移除1.0版本Config信息失败");
        }
    }

    public boolean hasNewInstall() {
        return sp.contains("isNewInstall");
    }

    public void writeSendDeviceInfo(boolean isSend) {
        SharedPreferences.Editor spEditor = sp.edit();
        //移除1.0版本的ptu上的配置信息，当时模块划分不清晰，也没考虑到模块会变大，变大之后这里变得复杂难写了
        spEditor.putBoolean("has_send_device", isSend).apply();
    }

    public boolean hasSendDeviceInfos() {
        return sp.getBoolean("has_send_device", false);
    }

    public static final String exportDataName = "单词笔记"; //数据导出文件名称
}

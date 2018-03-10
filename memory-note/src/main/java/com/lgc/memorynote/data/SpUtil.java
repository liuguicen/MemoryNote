package com.lgc.memorynote.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.lgc.memorynote.base.MemoryNoteApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * the util of SharedPreference
 * <pre>
 *      author : liuguicen
 *      time : 2018/03/10
 *      version : 1.0
 * <pre>
 */

public class SpUtil {
    private static String CMD_LIST = "cmd_list";
    private static String POSITION = "position";
    private static String USER_SP_NAME = "user";
    private static String UPLOAD_STATE = "upload state";

    /**
     * save last remember date, include the cmd list and the position of the word
     * @return if remember success
     */
    public static boolean saveCurRememberPosition(List<String> cmdList, int position) {
        SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences(USER_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putStringSet(CMD_LIST, new HashSet<>(cmdList));
        spEditor.putInt(POSITION, position);
        return spEditor.commit();
    }

    public static Pair<ArrayList<String>, Integer> getLastRemberState() {
        SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences(USER_SP_NAME, Context.MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet(CMD_LIST, new HashSet<String>());
        int position = sp.getInt(POSITION, -1);
        return new Pair<>(new ArrayList<>(stringSet), position);
    }

    public static boolean saveUploadState(String msg) {
        SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences(USER_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        return spEditor.putString(UPLOAD_STATE, msg).commit();
    }

    public static String getUploadState() {
        SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences(USER_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(UPLOAD_STATE, "");
    }
}

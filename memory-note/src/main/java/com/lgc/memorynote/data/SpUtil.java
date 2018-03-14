package com.lgc.memorynote.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.lgc.memorynote.base.MemoryNoteApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * the util of SharedPreference
 * <pre>
 *      author : liuguicen
 *      time : 2018/03/10
 *      version : 1.0
 * <pre>
 */

public class SpUtil {
    private static final String USER_NAME = "user_name";
    private static final String USER_PASSWORD = "user_password";
    private static final String CMD_LIST = "cmd_list";
    private static final String POSITION = "position";
    private static final String USER_SP_NAME = "user";
    private static final String UPLOAD_STATE = "upload state";
    private static final String RECENT_CMD = "recent_cmd";
    private static SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences(USER_SP_NAME, Context.MODE_PRIVATE);
    ;

    /**
     * save last remember date, include the cmd list and the position of the word
     *
     * @return if remember success
     */
    public static boolean saveCurRememberPosition(List<String> cmdList, int position) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putStringSet(CMD_LIST, new HashSet<>(cmdList));
        spEditor.putInt(POSITION, position);
        return spEditor.commit();
    }

    public static Pair<ArrayList<String>, Integer> getLastRemberState() {
        Set<String> stringSet = sp.getStringSet(CMD_LIST, new HashSet<String>());
        int position = sp.getInt(POSITION, -1);
        return new Pair<>(new ArrayList<>(stringSet), position);
    }

    public static boolean saveUploadState(String msg) {
        return sp.edit().putString(UPLOAD_STATE, msg).commit();
    }

    public static String getUploadState() {
        return sp.getString(UPLOAD_STATE, "");
    }

    public static List<String> getRecentCmdList() {
        String recentS = sp.getString(CMD_LIST, "");
        String[] split = recentS.split(Pattern.quote(AppConstant.RECENT_CMD_DIVIDER));
        List<String> recentCmdList = new ArrayList<>();
        for (String s : split) {
            s = s.trim();
            if (!s.isEmpty()) {
                recentCmdList.add(s);
            }
        }
        return recentCmdList;
    }

    public static boolean saveRecentCmd(List<String> cmdList) {
        StringBuilder sb = new StringBuilder();
        for (String s : cmdList) {
            s = s.trim();
            if (!s.isEmpty()) {
                sb.append(s).append(AppConstant.RECENT_CMD_DIVIDER);
            }
        }
        return sp.edit().putString(CMD_LIST, sb.toString()).commit();
    }

    public static String getUserName() {
        return sp.getString(USER_NAME, "");
    }

    public static boolean saveUserName(String userName) {
        return updateUserName(userName);
    }

    public static boolean saveUserPassword(String password) {
        return updateUserPassword(password);
    }

    public static boolean updateUserName(String userName) {
        if (userName == null || userName.isEmpty()) return false;
        return sp.edit().putString(USER_NAME, userName).commit();
    }

    public static String getUserPassword() {
        return sp.getString(USER_PASSWORD, "");
    }

    public static boolean updateUserPassword(String passWord) {
        if (passWord == null || passWord.isEmpty()) return false;
        return sp.edit().putString(USER_NAME, passWord).commit();
    }
}

package com.lgc.memorynote.user;


import android.text.TextUtils;

public class User {
    // 格式检查
    public static int NAME_IS_NULL = -1;
    public static int PASSWORD_IS_NULL = -2;
    public static int VALID = 1;
    private String mName;
    private String mPassword;
    private User() {

    }

    private static final class H{
        private static final User user = new User();
    }

    public static User getInstance() {
        return H.user;
    }

    public int setName(String name) {
        int state = checkName(name);
        if (state == VALID) {
            this.mName = name;
            return VALID;
        }
        return state;
    }

    public String getName() {
        return mName;
    }

    public int setPassword(String password) {
        int state = checkPassword(password);
        if (state == VALID) {
            this.mPassword = password;
            return VALID;
        }
        return state;
    }

    public String getPassword() {
        return mPassword;
    }

    public static int checkName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return NAME_IS_NULL;
        }
        // TODO: 2018/3/14 more check
        return VALID;
    }

    public static int checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return NAME_IS_NULL;
        }
        // TODO: 2018/3/14 more check
        return VALID;
    }
}

package com.lgc.wordanalysis.data;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 处理csv文件一些操作的工具
 */
public class CSVUtil {
    public static final boolean isTest = true;
    public static final String ROW_ORDER = "单词名  单词意思  单词陌生度   近似词列表  词组  近义词   记忆方法  上次记忆时间";
    private static void ld(String msg) {
        if (isTest) {
            Log.d("WordLibsUtil", msg == null ? "null" : msg);
        }
    }
}


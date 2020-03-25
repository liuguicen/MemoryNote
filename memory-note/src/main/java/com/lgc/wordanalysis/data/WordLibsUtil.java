package com.lgc.wordanalysis.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WordLibsUtil {


    public static final boolean isTest = true;
    /**
     * 目前是各个词汇表在Asset下面的文件名，两者必须保持一致
     * 更好的，直接从Assert下面获取文件名
     */
    public static String[] wordLibNameList = new String[]{
            "高考核心词-1000.csv",
            "高中词汇表-3500.csv",
            "四级高频词-660.csv",
            "四级词汇-3598.csv",
            "六级核心词-600.csv",
            "六级词汇-2088.csv",
            "考研核心词-3000.csv",
            "考研词汇表-5500.csv",
    };

}

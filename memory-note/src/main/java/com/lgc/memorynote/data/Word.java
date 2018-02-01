package com.lgc.memorynote.data;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/01/29
 *      version : 1.0
 * <pre>
 */

public class Word {
    /**
     * 单词
     */
    public String word;
    public int left;

    /**
     * 重要性
     */
    public int importance;
    public long addTime;
    public long updateTime;
    public float difficulty;
    /**
     * 陌生度，不熟悉的程度
     */
    public float strangeDegree;
    public MemoryMethod memoryMethod;


    /**
     * 单词的意思
     */
    public static class Meanging{
        public CiXing ciXing;
        public String meaning;
        public float rate;
        public String label;
    }

    /**
     * 记忆方法
     */
    public static class MemoryMethod {
        public String memeoryMethod;
    }

    /**
     * 词性
     */
    public static class CiXing{ //no translation
        public static final int  n     = 1;
        public static final int  v     = 2;
        public static final int  adj   = 3;
        public static final int  adv   = 4;
        public static final int  num   = 5;
        public static final int  art   = 6;
        public static final int  prep  = 7;
        public static final int  conj  = 8;
        public static final int  _int_ = 9;
    }
}

package com.lgc.wordanalysis.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/03/18
 *      version : 1.0
 * <pre>
 */

public class WordUtil {
    public static List<String> SUFFIX_LIST = new ArrayList<String>(){{
        add(Word.TAG_SUFFIX); add(Word.TAG_N_SUFFIX);
        add(Word.TAG_V_SUFFIX);add(Word.TAG_ADJ_SUFFIX);
        add(Word.TAG_ADV_SUFFIX);}};

    public static final double MATCH_BASE_INTERVAL = 0.01; // 扩展插入其它比较项时只要不小于这个即可
    public static final double MATCH_BASE_OTHER = 0.3;
    public static final double MATCH_BASE_GROUP = 0.4;
    public static final double MATCH_BASE_SIMILAR = 0.5;
    public static final double MATCH_BASE_STRANGE = 0.6;
    public static final double MATCH_BASE_MEANING = 0.7;
    public static final double MATCH_BASE_NAME = 0.8;


    public static double getHigherMatchDegree(double base, double degree) {
        return base + MATCH_BASE_INTERVAL * degree;
    }

    public static boolean isWord(String name) {
        if (name == null) return false;
        return !Pattern.compile(Word.NOT_WORD_REGEX).matcher(name).find();
    }

    /**
     * 广义的单词，包括单词，短语，词根词缀及其集合等
     */
    public static boolean isGeneralizedWord(String name) {
        if (name == null) return false;
        return !Pattern.compile(Word.NOT_NAME_PHRASE_REGEX).matcher(name).find();
    }
}

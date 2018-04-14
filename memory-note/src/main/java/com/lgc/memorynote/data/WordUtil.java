package com.lgc.memorynote.data;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/03/18
 *      version : 1.0
 * <pre>
 */

public class WordUtil {
    public static final double MATCH_BASE_INTERVAL = 0.01; // 扩展插入其它比较项时只要不小于这个即可
    public static final double MATCH_BASE_OTHER = 0.3;
    public static final double MATCH_BASE_GROUP = 0.4;
    public static final double MATCH_BASE_SIMILAR = 0.5;
    public static final double MATCH_BASE_STRANGE = 0.6;
    public static final double MATCH_BASE_MEANING = 0.7;
    public static final double MATCH_BASE_NAME = 0.8;


    /**
     * @return {@link Word#NORMAL} 等
     */
    public static int getWordType(Word word) {
        if (!Word.isLegalWordName(word.getName()))
            return Word.OTHER;
        if (word.getTagList() != null) {
            for (String s : word.getTagList()) {
                if (Word.TAG_ROOT.equals(s)) {
                    return Word.ROOT;
                } else if (Word.TAG_PREFFIX.equals(s)) {
                    return Word.PREFIX;
                } else if (Word.TAG_SUFFIX.equals(s)) {
                    return Word.SUFFIX;
                }
            }
        }
        return Word.NORMAL;
    }

    public static double getHigherMatchDegree(double base, double degree) {
        return base + MATCH_BASE_INTERVAL * degree;
    }
}

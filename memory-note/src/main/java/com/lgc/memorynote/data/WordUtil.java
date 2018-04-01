package com.lgc.memorynote.data;

import android.widget.TextView;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/03/18
 *      version : 1.0
 * <pre>
 */

public class WordUtil {

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
}

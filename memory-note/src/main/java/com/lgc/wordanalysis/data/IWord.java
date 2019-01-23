package com.lgc.wordanalysis.data;

import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/04/14
 *      version : 1.0
 * <pre>
 */

public interface IWord {
    String getName();

    boolean hasTag(String tag);

    int getStrangeDegree();

    long getLastRememberTime();

    List<Word.SimilarWord> getSimilarWordList();
}

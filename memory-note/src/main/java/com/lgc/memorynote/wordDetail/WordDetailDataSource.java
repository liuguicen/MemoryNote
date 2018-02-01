package com.lgc.memorynote.wordDetail;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 通知数据的接口
 */

public interface WordDetailDataSource {
    Word getWordDetail(String word);

    void addWord(Word word);

    void updateWord(Word word);

    Word getWordByName(String wordName);
}

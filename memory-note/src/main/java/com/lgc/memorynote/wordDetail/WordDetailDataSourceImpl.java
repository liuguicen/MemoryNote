package com.lgc.memorynote.wordDetail;

import android.content.Context;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class WordDetailDataSourceImpl implements WordDetailDataSource {
    Context appContext;

    WordDetailDataSourceImpl(Context context) {
        appContext = context;
    }

    @Override
    public Word getWordDetail(String word) {
        return null;
    }

    @Override
    public void addWord(Word word) {

    }

    @Override
    public void updateWord(Word word) {

    }
}

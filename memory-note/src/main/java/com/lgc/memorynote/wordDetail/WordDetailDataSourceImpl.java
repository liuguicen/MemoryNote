package com.lgc.memorynote.wordDetail;

import android.content.Context;

import com.lgc.memorynote.data.GlobalData;

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
        GlobalData.getInstance().addWord(word);
    }

    @Override
    public void deleteWord(Word word) {
        GlobalData.getInstance().deleteWord(word);
    }

    @Override
    public void updateWord(Word word) {
        GlobalData.getInstance().updateWord(word);
    }

    @Override
    public Word getWordByName(String wordName) {
        return null;
    }
}

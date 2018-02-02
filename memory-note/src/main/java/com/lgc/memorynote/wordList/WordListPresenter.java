package com.lgc.memorynote.wordList;

import android.content.Context;

import com.lgc.memorynote.data.GlobalWordData;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class WordListPresenter implements WordListContract.Presenter {
    Context mContext;
    public WordListPresenter(Context context) {
        this.mContext = context;
    }
    @Override
    public void start() {
        GlobalWordData.getInstance().getAllWord();
    }
}

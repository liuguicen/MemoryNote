package com.lgc.memorynote.wordList;

import android.content.Context;

import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.wordDetail.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class WordListPresenter implements WordListContract.Presenter {
    private final WordListContract.View mView;
    private Context mContext;
    private List<String> mInputCommandList = new ArrayList<>();
    private List<Word> mCurShowWordList = new ArrayList<>();

    public WordListPresenter(WordListContract.View view) {
        mView = view;
    }

    @Override
    public void start() {
        mCurShowWordList = GlobalData.getInstance().getAllWord();
        mView.showWordList(mCurShowWordList);
    }

    @Override
    public void addOneCommand(String command) {
        mInputCommandList.add(command);
        mView.showAddOneCommand(command);
    }

    /**
     * 点击搜索，重新组织单词列表
     */
    @Override
    public void reorderWordList() {
        mCurShowWordList = Command.orderByCommand(mInputCommandList, GlobalData.getInstance().getAllWord());
        mView.refreshWordList(mCurShowWordList);
        GlobalData.getInstance().updateCommandSort(mInputCommandList);
        mInputCommandList.clear();
        mView.updateCommandText(GlobalData.getInstance().getCommandList());
    }

    @Override
    public String getWordName(int adapterPosition) {
        return mCurShowWordList.get(adapterPosition).getWord();
    }
}
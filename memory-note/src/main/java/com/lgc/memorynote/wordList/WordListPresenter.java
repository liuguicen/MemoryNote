package com.lgc.memorynote.wordList;

import android.content.Context;
import android.text.TextUtils;

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
        mInputCommandList.add(SortUtil.DEFAULT_SORT_COMMAND);
    }

    @Override
    public void start() {
        mCurShowWordList = GlobalData.getInstance().getCurWords();
        mView.updateCommandText(GlobalData.getInstance().getCommandList(), mInputCommandList);
        reorderWordList(null);
    }

    @Override
    public void switchOneCommand(String command) {
        if (!mInputCommandList.contains(command)) {
            mInputCommandList.add(command);
        } else {
            mInputCommandList.remove(command);
        }
        mView.updateCommandText(GlobalData.getInstance().getCommandList(), mInputCommandList);
    }

    /**
     * 点击搜索，重新组织单词列表,搜索过程是种总的列表复制到新列表中，再对新列表进行排序，过滤
     * @param search
     */
    @Override
    public void reorderWordList(String search) {
        if (search !=  null)
            search = search.trim();
        if (!TextUtils.isEmpty(search)) {
            mInputCommandList.add(search);
        }
        mCurShowWordList = Command.orderByCommand(search, mInputCommandList, GlobalData.getInstance().getAllWord());
        GlobalData.getInstance().setCurWords(mCurShowWordList);
        mView.refreshWordList(mCurShowWordList);
        // GlobalData.getInstance().updateCommandSort(mInputCommandList);
    }

    @Override
    public String getWordName(int adapterPosition) {
        return mCurShowWordList.get(adapterPosition).getName();
    }

    @Override
    public void addStrange(int position) {
        Word word = mCurShowWordList.get(position);
        word.setStrangeDegree(word.strangeDegree + 1);
        GlobalData.getInstance().updateWord(word);
    }

    @Override
    public void reduceStrange(int position) {
        Word word = mCurShowWordList.get(position);
        word.setStrangeDegree(word.strangeDegree - 1);
        GlobalData.getInstance().updateWord(word);
    }
}
package com.lgc.memorynote.wordList;

import android.content.Context;

import com.lgc.memorynote.base.InputAnalyzerUtil;
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

    private List<String> mChosenCmdList = new ArrayList<>();
    private List<String> mInputCmdList = new ArrayList<>();
    private List<Word> mCurShowWordList = new ArrayList<>();

    public WordListPresenter(WordListContract.View view) {
        mView = view;
        mChosenCmdList.add(SortUtil.DEFAULT_SORT_COMMAND);
    }

    @Override
    public void start() {
        mCurShowWordList = GlobalData.getInstance().getCurWords();
        mView.updateCommandText(GlobalData.getInstance().getCommandList(), mChosenCmdList);
        reorderWordList(null);
    }

    @Override
    public void switchOneCommand(String command) {
        if (!mChosenCmdList.contains(command)) {
            mChosenCmdList.add(command);
        } else {
            mChosenCmdList.remove(command);
        }
        mView.updateCommandText(GlobalData.getInstance().getCommandList(), mChosenCmdList);
    }

    /**
     * 点击搜索，重新组织单词列表,搜索过程是种总的列表复制到新列表中，再对新列表进行排序，过滤
     * @param search
     */
    @Override
    public void reorderWordList(String search) {
        if (search !=  null)
            search = search.trim();
        mChosenCmdList.removeAll(mInputCmdList);
        mInputCmdList.clear(); //  clear last input cmd list first

        search = InputAnalyzerUtil.analyzeInputCommand(search, mInputCmdList);
        mChosenCmdList.addAll(mInputCmdList); // analyze and add current input cmd list


        mCurShowWordList = Command.orderByCommand(search, mChosenCmdList, GlobalData.getInstance().getAllWord());
        setUICommand(mChosenCmdList);
        GlobalData.getInstance().setCurWords(mCurShowWordList);
        mView.refreshWordList(mCurShowWordList);
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

    @Override
    public List<String> getChoseCommand() {
        return mChosenCmdList;
    }

    public void setUICommand(List<String> UICommand) {
        boolean isHideMeaning = false;
        boolean isHideWord = false;
        for (String s : UICommand) {
            if (Command._hdm.equals(s)) {
                isHideMeaning = true;
            } else if (Command._hdw.equals(s)) {
                isHideWord = true;
            }
        }
        mView.hideMeaning(isHideMeaning);
        mView.hideWord(isHideWord);
    }
}
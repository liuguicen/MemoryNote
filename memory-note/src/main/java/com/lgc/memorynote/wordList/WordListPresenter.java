package com.lgc.memorynote.wordList;

import android.content.Context;
import android.widget.Toast;

import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.data.SearchData;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SpUtil;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.user.User;

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

    private List<String> mCmdList = new ArrayList<>();
    private List<String> mInputCmdList = new ArrayList<>();
    private List<Word> mCurShowWordList = new ArrayList<>();
    private final GlobalData mGlobalData;
    private String mLastInputCmd;

    public WordListPresenter(WordListContract.View view) {
        mView = view;
        if (mView instanceof Context) {
            mContext = (Context)mView;
        }
        mCmdList.add(SortUtil.DEFAULT_SORT_COMMAND);
        mGlobalData = GlobalData.getInstance();
    }

    @Override
    public void start() throws Exception {
        mCurShowWordList = mGlobalData.getShowWords();
        mView.updateCommandText(Command.CLICKABLE_CMD_LIST, mCmdList);
        search();
    }

    @Override
    public void switchOneCommand(String command) {
        if (!mCmdList.contains(command)) {
            mCmdList.add(command);
        } else {
            mCmdList.remove(command);
        }
        mView.updateCommandText(Command.CLICKABLE_CMD_LIST, mCmdList);
    }

    /**
     * 点击搜索，重新组织单词列表,搜索过程是种总的列表复制到新列表中，再对新列表进行排序，过滤
     */
    public void search() throws Exception {
        search(true);
    }

    @Override
    public void search(boolean recordSearchData) throws Exception {
        if (Util.RepetitiveEventFilter.isRepetitive(500)) // 过滤重复的
            return;
        // 搜索之前，将上一次搜索的数据加进去
        if (recordSearchData) {
            mGlobalData.addLastSearchData(
                    new SearchData(mLastInputCmd, mCmdList, mView.getListPosition()));
        }

        String inputCmd = mView.getInputCmd();
        inputCmd = inputCmd != null ? inputCmd.trim() : "";
        recordInputCmd(inputCmd);

        int lastPosition = -1;

        // 第一步,输入框中的命令优先
        if (inputCmd.startsWith(Command._restore)) { // 恢复，放在第一步
            SearchData searchData = SpUtil.getLastSearchData();

            // 相当于重新输入了命令
            mView.showInputCmd(searchData.inputCmd);
            inputCmd = searchData.inputCmd != null ? searchData.inputCmd.trim() : "";
            recordInputCmd(inputCmd);

            mCmdList.clear();
            mCmdList.addAll(searchData.cmdList);
            mView.updateCommandText(Command.CLICKABLE_CMD_LIST, mCmdList);

            lastPosition = searchData.position;
        }

        if (inputCmd.startsWith(Command._open_setting)) {
            mView.setSettingActivity();
            mView.clearCommandEt();
            return;
        }

        if (inputCmd.startsWith(Command._word_number)) {
            mView.showWordNumber();
            return;
        }

        ArrayList<String> tempCmdList = new ArrayList<>(mCmdList);
        mCurShowWordList = Command.orderByCommand(inputCmd, tempCmdList, mGlobalData.getAllWord());
        doUICommand(tempCmdList); // 执行UI相关的命令
        mGlobalData.setCurWords(mCurShowWordList);
        mView.refreshWordList(mCurShowWordList);

        if (lastPosition >= 0) {
            mView.restorePosition(lastPosition);
        }
    }

    private void recordInputCmd(String inputCmd) {
        if (!inputCmd.isEmpty())
            mGlobalData.updateInputCmd(inputCmd); // 记录更新
        mLastInputCmd = inputCmd;
    }

    /**
     * 将当前命令以及位置记录下来，放到外存中，长期
     */
    public void saveSearchData() {
        boolean res = SpUtil.saveSearchData(new SearchData(mView.getInputCmd(), mCmdList, mView.getListPosition()));
        if (res) {
            Toast.makeText(mContext, "保存记录位置成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "保存记录位置失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String getWordName(int adapterPosition) {
        return mCurShowWordList.get(adapterPosition).getName();
    }

    @Override
    public void addStrange(int position) {
        Word word = mCurShowWordList.get(position);
        word.setStrangeDegree(word.strangeDegree + 1);
        mGlobalData.updateWord(word);
    }

    @Override
    public void reduceStrange(int position) {
        Word word = mCurShowWordList.get(position);
        word.setStrangeDegree(word.strangeDegree - 1);
        mGlobalData.updateWord(word);
    }

    @Override
    public List<String> getChoseCommand() {
        return mCmdList;
    }

    public void doUICommand(List<String> UICommand) {
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


    @Override
    public boolean checkUser() {
        if (mGlobalData.isCheckedUser()) // 每次应用启动只检查一次
            return true;
        if (User.checkName(mGlobalData.getUser().getName()) != User.VALID
                || User.checkPassword(mGlobalData.getUser().getPassword()) != User.VALID) {
            return false;
        }
        return true;
    }

    public void doPrevCmd() {
        SearchData searchData = mGlobalData.getPrevSearch();
        mView.showInputCmd(searchData.inputCmd);
        mCmdList = searchData.cmdList;
        mView.updateCommandText(Command.CLICKABLE_CMD_LIST, mCmdList);
        mView.onClickSearch(false);
    }
}
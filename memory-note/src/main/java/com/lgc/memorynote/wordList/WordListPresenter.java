package com.lgc.memorynote.wordList;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.lgc.memorynote.base.InputAnalyzerUtil;
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

    private List<String> mChosenCmdList = new ArrayList<>();
    private List<String> mInputCmdList = new ArrayList<>();
    private List<Word> mCurShowWordList = new ArrayList<>();
    private final GlobalData mGlobalData;

    public WordListPresenter(WordListContract.View view) {
        mView = view;
        if (mView instanceof Context) {
            mContext = (Context)mView;
        }
        mChosenCmdList.add(SortUtil.DEFAULT_SORT_COMMAND);
        mGlobalData = GlobalData.getInstance();
    }

    @Override
    public void start() throws Exception {
        mCurShowWordList = GlobalData.getInstance().getCurWords();
        mView.updateCommandText(Command.commandList, mChosenCmdList);
        reorderWordList();
    }

    @Override
    public void switchOneCommand(String command) {
        if (!mChosenCmdList.contains(command)) {
            mChosenCmdList.add(command);
        } else {
            mChosenCmdList.remove(command);
        }
        mView.updateCommandText(Command.commandList, mChosenCmdList);
    }

    /**
     * 点击搜索，重新组织单词列表,搜索过程是种总的列表复制到新列表中，再对新列表进行排序，过滤
     */
    @Override
    public void reorderWordList() throws Exception {
        String inputCmd = mView.getInputCmd();
        if (inputCmd != null) {
            inputCmd = inputCmd.trim();
        }
        int lastPosition = -1;
        if (inputCmd != null) {
            mGlobalData.updateInputCmd(inputCmd); // 记录更新
            if (inputCmd.startsWith(Command.OPEN_SETTING)) {
                mView.setSettingActivity();
                mView.clearCommandEt();
                return;
            }

            if (inputCmd.startsWith(Command.WORD_NUMBER)) {
                mView.showWordNumber();
                return;
            }

            if (inputCmd.startsWith(Command.RMB)) {
                saveCurRememberPosition();
                return;
            }

            if (inputCmd.startsWith(Command.RST)) {
                inputCmd = "";

                // get saved date
                Pair<ArrayList<String>, Integer> pair = SpUtil.getLastRemberState();

                if (!pair.first.isEmpty() && pair.second >= 0) {
                    lastPosition = pair.second;
                    // remove all cur chosen and add all saved cmd
                    mChosenCmdList.clear();
                    mChosenCmdList.addAll(pair.first);
                    mView.updateCommandText(Command.commandList, mChosenCmdList);
                }
            }
        }

        mChosenCmdList.removeAll(mInputCmdList);
        mInputCmdList.clear(); //  clear last input cmd list first

        inputCmd = InputAnalyzerUtil.analyzeInputCommand(inputCmd, mInputCmdList);
        mChosenCmdList.addAll(mInputCmdList); // analyze and add current input cmd list


        mCurShowWordList = Command.orderByCommand(inputCmd, mChosenCmdList, GlobalData.getInstance().getAllWord());
        setUICommand(mChosenCmdList);
        GlobalData.getInstance().setCurWords(mCurShowWordList);
        mView.refreshWordList(mCurShowWordList);

        if (lastPosition >= 0) {
            mView.restorePosition(lastPosition);
        }
    }

    private void saveCurRememberPosition() {
        boolean res = SpUtil.saveCurRememberPosition(mChosenCmdList, mView.getListPosition());
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
        GlobalData.getInstance().updateWord(word);
    }

    @Override
    public void reduceStrange(int position) {
        Word word = mCurShowWordList.get(position);
        word.setStrangeDegree(word.strangeDegree - 1);
        mGlobalData.updateWord(word);
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
}
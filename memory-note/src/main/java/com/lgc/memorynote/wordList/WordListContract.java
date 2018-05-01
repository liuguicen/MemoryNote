package com.lgc.memorynote.wordList;


import com.lgc.memorynote.base.BasePresenter;
import com.lgc.memorynote.base.BaseView;
import com.lgc.memorynote.data.Word;

import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/01/29
 *      version : 1.0
 * <pre>
 */

public class WordListContract {
    interface View extends BaseView<Presenter> {
        /**
         * 进入界面，并且获取到单词 信息之后开始显示
         * @param mCurShowWordList
         */
        void showWordList(List<Word> mCurShowWordList);

        void deleteOneWord(String s);

        void onTogglePicList(WordListAdapter wordListAdapter);

        /**
         * picAdapter通知数据更新了,刷新图片列表视图
         * @param resultList
         */
        void refreshWordList(List<Word> resultList);

        /**
         * 命令优先级改变了，重新显示
         */
        void updateCommandText(List<String> commandList, List<String> chosenList);

        void hideMeaning(boolean isHideMeaning);
        void hideWord(boolean isHideWord);

        void setSettingActivity();

        void showWordNumber();

        void showInputCmd(String inputCmd);

        void onClickSearch();

        int getListPosition();

        void restorePosition(int position);

        void clearCommandEt();

        String getInputCmd();
    }

    interface Presenter extends BasePresenter {
        /**
         *  选中或者去掉一个命令
         * @param command
         */
        void switchOneCommand(String command);

        /**
         * 点击搜索，重新组织单词列表
         */
        void search() throws Exception;

        String getWordName(int adapterPosition);

        void addStrange(int position);

        void reduceStrange(int position);

        List<String> getChoseCommand();

        boolean checkUser();
    }
}

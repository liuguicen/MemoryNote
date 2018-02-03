package com.lgc.memorynote.wordList;


import com.lgc.memorynote.base.BasePresenter;
import com.lgc.memorynote.base.BaseView;
import com.lgc.memorynote.wordDetail.Word;

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
         * 进入界面，并且获取到图片信息之后开始显示
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
    }
    interface Presenter extends BasePresenter {
        /**
         *  用户点击添加一个命令
         * @param command
         */
        void addOneCommand(String command);

        /**
         * 点击搜索，重新组织单词列表
         * @param search
         */
        void reorderWordList(String search);

        String getWordName(int adapterPosition);
    }
}

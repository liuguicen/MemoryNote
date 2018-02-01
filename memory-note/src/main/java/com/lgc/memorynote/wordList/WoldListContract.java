package com.lgc.memorynote.wordList;


import com.lgc.memorynote.base.BasePresenter;
import com.lgc.memorynote.base.BaseView;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/01/29
 *      version : 1.0
 * <pre>
 */

public class WoldListContract {
    interface View extends BaseView<WordListPresenter> {
        /**
         * 进入界面，并且获取到图片信息之后开始显示
         */
        void showWordList();

        void deleteOneWord(String s);

        void onTogglePicList(WordListAdapter wordListAdapter);

        /**
         * picAdapter通知数据更新了,刷新图片列表视图
         */
        void refreshPicList();
    }
    interface WordListPresenter extends BasePresenter {

    }
}

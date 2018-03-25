package com.lgc.memorynote.wordDetail;

import android.content.Intent;

import com.lgc.memorynote.base.BasePresenter;
import com.lgc.memorynote.base.BaseView;
import com.lgc.memorynote.data.Word;

import java.util.List;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 主持者和View契约类
 */

public interface WordDetailContract {
    interface View extends BaseView<Presenter>, android.view.View.OnClickListener, android.view.View.OnLongClickListener{

        void showWordName(String word);

        void showWordMeaning(Word  word);

        void showInputMeaning(String inputMeaning);

        void showSimilarWords(List<Word.SimilarWord> similarWordList);

        void showInputSimilarWords(String inputSimilarWords);

        void showInputRememberWay(String rememberWay);

        void showWordGroupList(List<Word.SimilarWord> groupList);

        void showInputWordGroup(String wordGroup);

        void showStrangeDegree(int strangeDegree);

        void showLastRememberTime(long lastRememberTime);

        void switchEditStyle(boolean mIsInEdit);

        String getInputWordName();
        String getInputWordMeaning();
        String getInputSimilarWords();
        String getInputRememberWay();
        String getInputWordGroup();

        void showSaveFailed(int state);

        void showInvalidName(int state);

        void showAnalyzeFailed(int resultCode);

        void showRememberWay(String rememberWay);

        /** 新增某种类型的单词时辅助填写内容 **/
        void setInputAssistant();

    }

    interface Presenter extends BasePresenter {

        boolean isInEdit();

        void switchEdit();

        /**
         * 返回，增加或者删除是否超限
         */
        boolean addStrangeDegree();

        boolean reduceStrangeDegree();


        void checkWordValidity();

        /**
         * 同步相似单词，也就是获取其它单词中相似单词列表中包含这个单词的，将那个单词的列表同步的这上面来
         */
        void syncSimilarWord();

        void syncWordGroup();

        void syncRootAffix();

        /**
         * @return 加入的单词是否是有效单词
         */
        boolean addWord(String word);

        void setLastRememberTime();

        void initAndShowData(Intent intent);

        void deleteWord();

        boolean isRefreshList();

    }
}

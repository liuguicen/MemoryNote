package com.lgc.memorynote.wordDetail;

import android.content.Intent;

import com.lgc.memorynote.base.BasePresenter;
import com.lgc.memorynote.base.BaseView;

import java.util.List;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 主持者和View契约类
 */

public interface WordDetailContract {
    interface View extends BaseView<Presenter>, android.view.View.OnClickListener {

        void showWord(String word);

        void showWordMeaning(List<Word.WordMeaning> wordMeaningList);

        void showInputMeaning(String inputMeaning);

        void showSimilarWords(List<Word.SimilarWord> similarWordList);

        void showInputSimilarWords(String inputSimilarWords);

        void showStrangeDegree(int strangeDegree);

        void showLastRememberTime(long lastRememberTime);

        void switchEditStyle(boolean mIsInEdit);

        String getInputWordName();
        String getInputWordMeaning();
        String getInputSimilarWords();

        void showSaveFailed(int state);

        void showAnalyzeFailed(int resultCode);
    }

    interface Presenter extends BasePresenter {

        boolean isInEdit();

        void switchEdit();

        /**
         * 返回，增加或者删除是否超限
         */
        boolean addStrangeDegree();

        boolean reduceStrangeDegree();

        /**
         * @return 加入的单词是否是有效单词
         */
        boolean addWord(String word);

        void setLastRememberTime();

        void initAndShowData(Intent intent);

        void deleteWord();
    }

}

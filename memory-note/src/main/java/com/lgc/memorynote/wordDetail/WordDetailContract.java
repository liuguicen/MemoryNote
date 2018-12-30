package com.lgc.memorynote.wordDetail;

import android.content.Intent;

import com.lgc.baselibrary.baseComponent.BaseContract;
import com.lgc.baselibrary.baseComponent.BaseView;
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

        void showIsCheckedMeaning(boolean checkedMeaning);

        void showInputMeaning(String inputMeaning);

        void showSimilarWords(List<Word.SimilarWord> similarWordList);

        void showInputSimilarWords(String inputSimilarWords, boolean isSelect);

        void showInputRememberWay(String rememberWay);

        void showWordGroupList(List<Word.SimilarWord> groupList);

        void showInputWordGroup(String wordGroup, boolean isSelect);

        void showSynonymList(List<Word.SimilarWord> synonymList);

        void showInputSynonym(String wordGroup, boolean isSelect);

        void showStrangeDegree(int strangeDegree);

        void showLastRememberTime(long lastRememberTime);

        void switchEditStyle(boolean mIsInEdit);

        String getInputWordName();
        String getInputWordMeaning();
        String getInputSimilarWords();
        String getInputRememberWay();
        String getInputWordGroup();
        String getInputSynonym();

        void showSaveFailed(int state);

        void showInvalidName(int state);

        void showAnalyzeFailed(int resultCode);

        void showRememberWay(String rememberWay, boolean isSelect);

        /** 新增某种类型的单词时辅助填写内容 **/
        void setInputAssistant();
    }

    interface Presenter extends BaseContract.BasePresenter {

        boolean isInEdit();

        void switchEdit();

        void setCheckMeaning();

        /**
         * 返回，增加或者删除是否超限
         */
        boolean addStrangeDegree();

        boolean reduceStrangeDegree();


        void checkWordValidity();

        boolean checkWordRepeat();

        /**
         * 同步相似单词，也就是获取其它单词中相似单词列表中包含这个单词的，将那个单词的列表同步的这上面来
         */
        void syncSimilarWord();

        void syncWordGroup();

        void syncSynonyms();

        void syncRootAffix();

        /**
         * @return 加入的单词是否是有效单词
         */
        boolean addWord(String word);

        void setLastRememberTime();

        void initAndShowData(Intent intent);

        void deleteWord();

        boolean isRefreshList();

        void setStrangeDegree(int i);

        void saveInputAssistant();

        void setClickName();
    }
}

package com.lgc.memorynote.wordDetail;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import static com.lgc.memorynote.WordDetailActivity.INTENT_EXTRA_IS_ADD;
import static com.lgc.memorynote.WordDetailActivity.INTENT_EXTRA_WORD_NAME;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class WordDetailPresenter implements WordDetailContract.Presenter {
    private WordDetailDataSource mDataSource;
    private WordDetailContract.View mView;
    private boolean mIsAdd = false;
    private boolean mIsInEdit = false;
    private Context mContext;
    private Word mWord;

    WordDetailPresenter(WordDetailDataSource wordDetailDataSource, WordDetailContract.View wordDetailView) {
        mDataSource = wordDetailDataSource;
        mView = wordDetailView;
        mContext = (Context) mView;
        mWord = new Word();
    }

    @Override
    public void initDate(Intent intent) {
        mIsAdd = intent.getBooleanExtra(INTENT_EXTRA_IS_ADD, false);
        if (mIsAdd) {
            mWord = new Word();
            if (!mIsInEdit) {
                switchEdit();
            }
        } else {
            String wordName = intent.getStringExtra(INTENT_EXTRA_WORD_NAME);
            mWord = mDataSource.getWordByName(wordName);
            showData(false);
        }
    }

    public void saveWordDate() {
        if (mIsAdd) {
            mDataSource.addWord(mWord);
        } else {
            mDataSource.updateWord(mWord);
        }
    }

    @Override
    public boolean isInEdit() {
        return mIsInEdit;
    }

    @Override
    public void switchEdit() {
        mIsInEdit = !mIsInEdit;
        if (!mIsInEdit) { // 编辑完成
            saveWordDate();
        }
        showData(true);
        mView.switchEdit(mIsInEdit);
    }

    private void showData(boolean isSwitchEdit) {
        if (mIsInEdit) {
            mView.showInputSimilarWords(mWord.getInputSimilarWords());
            mView.showInputMeanig(mWord.getInputMeaning());
        } else {
            mView.showWordMeaning(mWord.getMeaningList());
            mView.showSimilarWords(mWord.getSimilarWordList());
        }
        if (!isSwitchEdit) { // 切换编辑的过程中，这些视图的数据不用变
            mView.showWord(mWord.getWord());
            mView.showStrangeDegree(mWord.getStrangeDegree());
            mView.showLastRememberTime(mWord.getLastRememberTime());
        }
    }


    @Override
    public boolean addStrangeDegree() {
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public boolean reduceStrangeDegree() {
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public void setSimilarFormatWords(String inputSimilarWord) {
        List<String> similarWordList = new ArrayList<>();
        WordAnalyzer.analyzeSimilarWordsFromUser(inputSimilarWord, similarWordList);
        mWord.setInputSimilarWords(inputSimilarWord);
        mWord.setSimilarWordList(similarWordList);
    }

    @Override
    public void setWordMeaning(String inputMeaning) {
        List<Word.WordMeaning> meaningList = new ArrayList<>();
        WordAnalyzer.analyzeMeaningFromUser(inputMeaning, meaningList);
        mWord.setMeaningList(meaningList);
        mWord.setInputMeaning(inputMeaning);
    }

    @Override
    public boolean addWord(String word) {
        return false;
    }

    @Override
    public void setLastRememberTime() {
        mWord.lastRememberTime = System.currentTimeMillis();
    }

    @Override
    public void start() {

    }
}

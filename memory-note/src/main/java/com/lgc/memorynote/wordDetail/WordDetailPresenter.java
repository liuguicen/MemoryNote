package com.lgc.memorynote.wordDetail;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.data.AppConstant;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SearchUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class WordDetailPresenter implements WordDetailContract.Presenter {
    private WordDetailContract.View mView;
    private boolean mIsAdd = false;
    private boolean mIsInEdit = false;
    private Context mContext;
    private Word mWord;

    WordDetailPresenter(WordDetailContract.View wordDetailView) {
        mView = wordDetailView;
        mContext = (Context) mView;
        mWord = new Word();
    }

    @Override
    public void initAndShowData(Intent intent) {
        mIsAdd = intent.getBooleanExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, false);
        if (mIsAdd) {
            mWord = new Word();
            if (!mIsInEdit) {
                switchEdit();
            }
            setLastRememberTime();
            setStrangeDegree(10);
        } else {
            String wordName = intent.getStringExtra(WordDetailActivity.INTENT_EXTRA_WORD_NAME);
            mWord = SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), wordName);
            if (mWord == null)
                mWord = new Word();
            showData(false);
        }
    }

    public void saveWordDate() {
        String inputMeaings = mView.getInputWordMeaning().trim();
        String inputSimilars = mView.getInputSimilarWords().trim();
        if (mIsAdd) {
            String inputName = mView.getInputWordName();
            inputName = inputName.trim();
            while(Pattern.compile("[^a-zA-z- ]").matcher(inputName).find()) {
                mView.showSaveFailed(AppConstant.WORD_FORMAT_ERROR);
                return;
            } if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), mWord.getName()) != null) {
                mView.showSaveFailed(AppConstant.REPETITIVE_WORD);
            }
            mWord.setName(inputName);
        }
        if (!TextUtils.equals(inputMeaings, mWord.getInputMeaning())) {
            mWord.setInputMeaning(inputMeaings);
            List<Word.WordMeaning> meaningList = new ArrayList<>();
            int resultCode = InputAnalyzerUtil.analyzeMeaningFromUser(inputMeaings, meaningList);
            if (resultCode != InputAnalyzerUtil.SUCCESS) {
                mView.showAnalyzeFailed(resultCode);
            } else {
                mWord.setMeaningList(meaningList);
            }
        }
        if (!TextUtils.equals(inputSimilars, mWord.getInputSimilarWords())) {
            mWord.setInputSimilarWords(inputSimilars);
            List<String> similarList = new ArrayList<>();
            InputAnalyzerUtil.analyzeSimilarWordsFromUser(inputSimilars, similarList);
            mWord.setSimilarWordList(similarList);
        }

        if (mIsAdd) {
            GlobalData.getInstance().addWord(mWord);
        } else {
            GlobalData.getInstance().updateWord(mWord);
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
        mView.switchEditStyle(mIsInEdit);
    }

    private void showData(boolean isSwitchEdit) {
        if (mIsInEdit) {
            mView.showInputMeaning(mWord.getInputMeaning());
            mView.showInputSimilarWords(mWord.getInputSimilarWords());
        } else {
            mView.showWordMeaning(mWord.getMeaningList());
            mView.showSimilarWords(mWord.getSimilarWordList());
        }
        if (!isSwitchEdit) { // 切换编辑的过程中，这些视图的数据不用变
            mView.showWord(mWord.getName());
            mView.showStrangeDegree(mWord.getStrangeDegree());
            mView.showLastRememberTime(mWord.getLastRememberTime());
        }
    }


    @Override
    public boolean addStrangeDegree() {
        mWord.strangeDegree ++;
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public boolean reduceStrangeDegree() {
        mWord.strangeDegree --;
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public void setSimilarFormatWords(String inputSimilarWord) {
        List<String> similarWordList = new ArrayList<>();
        InputAnalyzerUtil.analyzeSimilarWordsFromUser(inputSimilarWord, similarWordList);
        mWord.setInputSimilarWords(inputSimilarWord);
        mWord.setSimilarWordList(similarWordList);
    }

    @Override
    public boolean addWord(String word) {
        return false;
    }

    @Override
    public void setLastRememberTime() {
        mWord.lastRememberTime = System.currentTimeMillis();
        mView.showLastRememberTime(mWord.lastRememberTime);
    }

    @Override
    public void start() {

    }

    public void setStrangeDegree(int strangeDegree) {
        mWord.strangeDegree = strangeDegree;
        mView.showStrangeDegree(mWord.strangeDegree);
    }
}
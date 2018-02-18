package com.lgc.memorynote.wordDetail;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.base.network.NetWorkUtil;
import com.lgc.memorynote.data.AppConstant;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
            mView.switchEditStyle(false);
            showData(false);
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

    public void saveWordDate() {
        String inputMeaings = mView.getInputWordMeaning().trim();
        String inputSimilars = mView.getInputSimilarWords().trim();
        String inputRememberWay = mView.getInputRememberWay().trim();
        String inputWordGroup = mView.getInputWordGroup().trim();

        // 名字相关
        String inputName = mView.getInputWordName();
        inputName = inputName.trim();
        if (inputName.isEmpty()) {
            mView.showSaveFailed(AppConstant.WORD_IS_NULL);
            return;
        }
        if (Pattern.compile(Word.NOT_NAME_FORMAT_REGEX).matcher(inputName).find()) {
            mView.showSaveFailed(AppConstant.WORD_FORMAT_ERROR);
            return;
        }
        if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), inputName) != null) {
            mView.showSaveFailed(AppConstant.REPETITIVE_WORD);
            return;
        }
        if (!inputName.equals(mWord.getName())) {  // 名字发生变动，视为添加
            mIsAdd = true;
            if (!TextUtils.isEmpty(mWord.getName())) {
                GlobalData.getInstance().deleteWord(mWord); // 删掉旧的word
            }
        }
        if (mIsAdd) {
            mWord.setName(inputName);
        }

        // 其他输入
        if (!TextUtils.equals(inputMeaings, mWord.getInputMeaning())) {
            mWord.setInputMeaning(inputMeaings);
            List<Word.WordMeaning> meaningList = new ArrayList<>();
            int resultCode = InputAnalyzerUtil.analyzeInputMeaning(inputMeaings, meaningList);
            if (resultCode != InputAnalyzerUtil.SUCCESS) {
                mView.showAnalyzeFailed(resultCode);
            } else {
                mWord.setMeaningList(meaningList);
            }
        }
        if (!TextUtils.equals(inputSimilars, mWord.getInputSimilarWords())) {
            mWord.setInputSimilarWords(inputSimilars);
            List<Word.SimilarWord> similarList = new ArrayList<>();
            InputAnalyzerUtil.analyzeInputSimilarWords(inputSimilars, similarList);
            mWord.setSimilarWordList(similarList);
        }

        if (!TextUtils.equals(inputRememberWay, mWord.getInputRememberWay())) {
            mWord.setInputRememberWay(inputRememberWay);
        }

        if (!TextUtils.equals(inputWordGroup, mWord.getInputWordGroup())) {
            mWord.setInputWordGroup(inputWordGroup);
            List<Word.SimilarWord> groupList = new ArrayList<>();
            InputAnalyzerUtil.analyzeInputSimilarWords(inputWordGroup, groupList);
            mWord.setGroupList(groupList);
        }

        mWord.setLastModifyTime(System.currentTimeMillis());

        if (mIsAdd) {
            GlobalData.getInstance().addWord(mWord);
        } else {
            GlobalData.getInstance().updateWord(mWord);
        }
        mIsAdd = false; // 保存一次之后就不再是true了
    }

    private void showData(boolean isSwitchEdit) {
        if (mIsInEdit) {
            mView.showInputMeaning(mWord.getInputMeaning());
            mView.showInputSimilarWords(mWord.getInputSimilarWords());
            mView.showInputRememberWay(mWord.getInputRememberWay());
            mView.showInputWordGroup(mWord.getInputWordGroup());
        } else {
            mView.showWordMeaning(mWord.getMeaningList());
            mView.showSimilarWords(mWord.getSimilarWordList());
            mView.showInputRememberWay(mWord.getInputRememberWay());
            mView.showWordGroupList(mWord.getGroupList());
        }
        if (!isSwitchEdit) { // 切换编辑的过程中，这些视图的数据不用变
            mView.showWord(mWord.getName());
            mView.showStrangeDegree(mWord.getStrangeDegree());
            mView.showLastRememberTime(mWord.getLastRememberTime());
        }
    }


    @Override
    public void syncSimilarWord() {
        String similar = syncChildWord(
                mView.getInputSimilarWords(),
                SearchUtil.searchAllSimilars(GlobalData.getInstance().getAllWord(), mWord.getName()));
        mView.showInputSimilarWords(similar);
    }

    @Override
    public void syncWordGroup() {
        String groupString = syncChildWord(
                mView.getInputWordGroup(),
                SearchUtil.searchAllGroups(GlobalData.getInstance().getAllWord(), mWord.getName()));
        mView.showInputWordGroup(groupString);
    }

    private String syncChildWord(String inputChildWord, Set<Word.SimilarWord> searchWord) {

        // 解析出已经存在的
        List<Word.SimilarWord> exitSimilar = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputSimilarWords(inputChildWord, exitSimilar);

        // 在去除重复的
        searchWord.removeAll(exitSimilar);
        if (searchWord.isEmpty())
            return  inputChildWord;

        if (!mIsInEdit) {
            switchEdit();
        }
        return  UIUtil.joinSimilar(inputChildWord, new ArrayList<>(searchWord));
    }


    @Override
    public boolean addStrangeDegree() {
        mWord.strangeDegree++;
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public boolean reduceStrangeDegree() {
        mWord.strangeDegree--;
        mView.showStrangeDegree(mWord.strangeDegree);
        return false;
    }

    @Override
    public boolean addWord(String word) {
        return false;
    }

    @Override
    public void setLastRememberTime() {
        mWord.setLastRememberTime(System.currentTimeMillis());
        mView.showLastRememberTime(mWord.getLastRememberTime());
    }

    @Override
    public void start() {

    }

    public void setStrangeDegree(int strangeDegree) {
        mWord.strangeDegree = strangeDegree;
        mView.showStrangeDegree(mWord.strangeDegree);
    }

    @Override
    public void deleteWord() {
        GlobalData.getInstance().deleteWord(mWord);
    }
}
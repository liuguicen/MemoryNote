package com.lgc.memorynote.wordDetail;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.TextureView;

import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.data.AppConstant;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.data.Word;

import org.w3c.dom.Text;

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
            String recevName = intent.getStringExtra(WordDetailActivity.INTENT_EXTRA_ADD_NAME);
            if (recevName != null) {
                mWord.setName(recevName);
                mView.showWordName(recevName);
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
        if (!inputName.equals(mWord.getName())) {  // 名字发生变动，视为添加
            // 检查添加的名字是否重复，若重复则什么动作都不做
            if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), inputName) != null) {
                mView.showSaveFailed(AppConstant.REPETITIVE_WORD);
                return;
            }
            mIsAdd = true;
            if (!TextUtils.isEmpty(mWord.getName())) {
                GlobalData.getInstance().deleteWord(mWord); // 删掉旧的word
            }
        }
        if (mIsAdd) {
            mWord.setName(inputName);
        }

        mWord.setLastModifyTime(System.currentTimeMillis());
        if (mIsAdd) {
            GlobalData.getInstance().addWord(mWord);
        } else {
            GlobalData.getInstance().updateWord(mWord);
        }
        mIsAdd = false; // 保存一次之后就不再是true了
    }

    @Override
    public void checkWordValidity() {
        // 名字相关
        String inputName = mView.getInputWordName();
        inputName = inputName.trim();
        if (inputName.isEmpty()) {
            mView.showInvalidName(AppConstant.WORD_IS_NULL);
            return;
        }
        if (Pattern.compile(Word.NOT_NAME_FORMAT_REGEX).matcher(inputName).find()) {
            mView.showInvalidName(AppConstant.WORD_FORMAT_ERROR);
            return;
        }
        if (!inputName.equals(mWord.getName())) {  // 名字发生变动
            // 检查添加的名字是否重复
            if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), inputName) != null) {
                mView.showInvalidName(AppConstant.REPETITIVE_WORD);
                return;
            }
        }
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
            mView.showWordName(mWord.getName());
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

    private String syncChildWord(String inputChildWord, Set<Word.SimilarWord> searchWordList) {

        // 解析出已经存在的
        List<Word.SimilarWord> exitSimilar = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputSimilarWords(inputChildWord, exitSimilar);

        // the exit word which don't input meaning, search meaning in global list, add meaning to it
        for (Word.SimilarWord similarWord : exitSimilar) {
            if(TextUtils.isEmpty(similarWord.getAnotation())) {
                Word oneWordByName = SearchUtil.getOneWordByName(
                        GlobalData.getInstance().getAllWord(), similarWord.getName()
                );
                if (oneWordByName != null) {
                    List<Word.WordMeaning> meaningList = oneWordByName.getMeaningList();
                    similarWord.setAnotation(UIUtil.meaningList2String(meaningList));
                    inputChildWord = inputChildWord.replace(similarWord.getName(), similarWord.getName()
                            + "  " + similarWord.getAnotation());
                }
            }
        }

        // 在去除重复的
        searchWordList.removeAll(exitSimilar);
        if (searchWordList.isEmpty())
            return  inputChildWord;

        if (!mIsInEdit) {
            switchEdit();
        }
        return  UIUtil.joinSimilar(inputChildWord, new ArrayList<>(searchWordList));
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
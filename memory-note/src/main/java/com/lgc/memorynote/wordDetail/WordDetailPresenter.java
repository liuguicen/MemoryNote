package com.lgc.memorynote.wordDetail;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import com.lgc.memorynote.base.AlgorithmUtil;
import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.data.AppConstant;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.data.SpUtil;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.data.WordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class WordDetailPresenter implements WordDetailContract.Presenter {
    private WordDetailContract.View mView;
    private boolean mIsAdd = false;
    private boolean mIsInEdit = false;
    private Context mContext;
    private Word mWord;
    private boolean isRefreshList = false;
    private boolean mIsClickName = false;

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
            String assistantKey =  SpUtil.getInputAssistantKey();
            if (!assistantKey.trim().isEmpty()) {
                Word assistantWord = SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), assistantKey);
                if (assistantWord != null) {
                    showData(assistantWord, false);
                }
            }

            String recevName = intent.getStringExtra(WordDetailActivity.INTENT_EXTRA_ADD_NAME);
            if (recevName != null) {
                mWord.setName(recevName);
                mView.showWordName(recevName);
            }
        } else {
            String wordName = intent.getStringExtra(WordDetailActivity.INTENT_EXTRA_WORD_NAME);
            mWord = SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), wordName);
            if (mWord == null)
                mWord = new Word();
            mView.switchEditStyle(false);
            showData(mWord,
                    false);
        }
    }

    @Override
    public void setClickName() {
        mIsClickName = true;
    }

    @Override
    public boolean isInEdit() {
        return mIsInEdit;
    }

    @Override
    public void switchEdit() {
        isRefreshList = true;
        mIsInEdit = !mIsInEdit;
        if (!mIsInEdit) { // 编辑完成
            saveWordDate();
        }
        showData(mWord, true);
        mView.switchEditStyle(mIsInEdit);
    }

    public void saveWordDate() {
        String inputMeaings = mView.getInputWordMeaning().trim();
        String inputSimilars = mView.getInputSimilarWords().trim();
        String inputRememberWay = mView.getInputRememberWay().trim();
        String inputWordGroup = mView.getInputWordGroup().trim();
        String inputSynonym = mView.getInputSynonym().trim();

        // 其他输入
        if (!TextUtils.equals(inputMeaings, mWord.getInputMeaning())) {
            mWord.setInputMeaning(inputMeaings);
            int resultCode = InputAnalyzerUtil.analyzeInputMeaning(inputMeaings, mWord);
            if (resultCode != InputAnalyzerUtil.SUCCESS) {
                mView.showAnalyzeFailed(resultCode);
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

        if (!TextUtils.equals(inputSynonym, mWord.getInputSynonym())) {
            mWord.setInputSynonyms(inputSynonym);
            List<Word.SimilarWord> synonymList = new ArrayList<>();
            InputAnalyzerUtil.analyzeInputSimilarWords(inputSynonym, synonymList);
            mWord.setSynonymList(synonymList);
        }

        // 名字相关
        String inputName = mView.getInputWordName();
        inputName = inputName.trim();
        if (inputName.isEmpty()) {
            mView.showSaveFailed(AppConstant.WORD_IS_NULL);
            return;
        }
//        if (Pattern.compile(Word.NOT_NAME_PHRASE_REGEX).matcher(inputName).find()) {
//            mView.showSaveFailed(AppConstant.WORD_FORMAT_ERROR);
//            return;
//        }
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
            if(mWord.hasTag(Word.TAG_DI)) {
                mWord.setStrangeDegree(Word.DEGREE_DI);
            } else if (mWord.hasTag(Word.TAG_ROOT)) {
                mWord.setStrangeDegree(Word.DEGREE_ROOT);
            } else if (mWord.hasTag(Word.TAG_PREFFIX)) {
                mWord.setStrangeDegree(Word.DEGREE_PREFFIX);
            } else if (mWord.hasTags(WordUtil.SUFFIX_LIST)) {
                mWord.setStrangeDegree(Word.DEGREE_SUFFIX);
            } else if (mWord.hasTag(Word.TAG_WEI)) {
                mWord.setStrangeDegree(Word.DEGREE_WEI);
            } else if (mWord.hasTag(Word.TAG_FEI)) {
                mWord.setStrangeDegree(Word.DEGREE_FEI);
            }
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
//        if (Pattern.compile(Word.NOT_NAME_PHRASE_REGEX).matcher(inputName).find()) {
//            mView.showInvalidName(AppConstant.WORD_FORMAT_ERROR);
//            return;
//        }
        if (!inputName.equals(mWord.getName())) {  // 名字发生变动
            // 检查添加的名字是否重复
            if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), inputName) != null) {
                mView.showInvalidName(AppConstant.REPETITIVE_WORD);
                return;
            }
        }
    }

    public boolean checkWordRepeat() {
        if (mIsClickName) {
            String inputName = mView.getInputWordName().trim();
            if (mIsAdd || !inputName.equals(mWord.getName())) {  // 名字发生变动
                if (SearchUtil.getOneWordByName(GlobalData.getInstance().getAllWord(), inputName) != null) {
                    mView.showSaveFailed(AppConstant.REPETITIVE_WORD_CHECKED);
                    return true;
                }
            }
            mIsClickName = false;
        }
        return false;
    }

    private void showData(Word word, boolean isSwitchEdit) {
        if (mIsInEdit) {
            mView.showInputMeaning(word.getInputMeaning());
            mView.showInputSimilarWords(word.getInputSimilarWords(), false);
            mView.showInputRememberWay(word.getInputRememberWay());
            mView.showInputWordGroup(word.getInputWordGroup(), false);
            mView.showInputSynonym(word.getInputSynonym(), false);
        } else {
            mView.showWordMeaning(word);
            mView.showSimilarWords(word.getSimilarWordList());
            mView.showInputRememberWay(word.getInputRememberWay());
            mView.showWordGroupList(word.getGroupList());
            mView.showSynonymList(word.getSynonymList());
        }
        if (!isSwitchEdit) { // 切换编辑的过程中，这些视图的数据不用变
            mView.showWordName(word.getName());
            mView.showStrangeDegree(word.getStrangeDegree());
            mView.showLastRememberTime(word.getLastRememberTime());
            mView.showIsCheckedMeaning(word.isCheckedMeaning());
        }
    }


    @Override
    public void syncSimilarWord() {
        String similar = syncChildWord(
                mView.getInputSimilarWords(),
                SearchUtil.searchAllSimilars(GlobalData.getInstance().getAllWord()
                        , mView.getInputWordName().trim()));
        mView.showInputSimilarWords(similar, true);
    }

    @Override
    public void syncWordGroup() {
        String groupString = syncChildWord(
                mView.getInputWordGroup(),
                SearchUtil.searchAllGroups(GlobalData.getInstance().getAllWord(), mWord
                        , mView.getInputWordName().trim()));
        mView.showInputWordGroup(groupString, true);
    }

    @Override
    public void setCheckMeaning() {
        mWord.setCheckedMeaning(!mWord.isCheckedMeaning);
        mView.showIsCheckedMeaning(mWord.isCheckedMeaning);
    }

    @Override
    public void syncSynonyms() {
        List<String> srcMeanUnit = AlgorithmUtil.StringAg.splitChineseWord(
                mWord.getInputMeaning() + " " + mView.getInputSynonym());
        for (int i = srcMeanUnit.size() - 1; i >= 0; i--) {
            String s = srcMeanUnit.get(i);
            if (s.startsWith("使")
                    ||s.startsWith("把")
                    ||s.startsWith("谐音")
                    ||s.startsWith("源于")
                    ||s.startsWith("给")) {
                srcMeanUnit.remove(i);
            }
        }
        Set<Word.SimilarWord> searchWordList = SearchUtil.searchAllSynonym(
                GlobalData.getInstance().getAllWord(), mWord
                ,mView.getInputWordName().trim(), srcMeanUnit);

        String synonymString = syncChildWord(mView.getInputSynonym(), searchWordList);
        mView.showInputSynonym(synonymString, true);
    }

    @Override
    public void syncRootAffix() {
        // 查找词根词缀的词组
        // key = Integer 用于区分词根，前缀，后缀三种类型
        ArrayList<Pair<Integer, String>> rootAffixList  = SearchUtil.searchRootAffix(
                GlobalData.getInstance().getAllWord(), mWord.getName());

        if (rootAffixList != null && !rootAffixList.isEmpty() && !mIsInEdit) {
            switchEdit();
        }
        String rememberWay = UIUtil.joinRememberWay(mView.getInputRememberWay(), rootAffixList);
        mView.showRememberWay(rememberWay, true);
    }

    private String syncChildWord(String inputChildWord, Set<Word.SimilarWord> searchWordList) {

        // 解析出已经存在的
        List<Word.SimilarWord> exitSimilar = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputSimilarWords(inputChildWord, exitSimilar);

        boolean hasModify = false;
        // the exit word which don't input meaning, search meaning in global list, add meaning to it
        for (Word.SimilarWord similarWord : exitSimilar) {
            if(TextUtils.isEmpty(similarWord.getAnotation())) {
                Word oneWordByName = SearchUtil.getOneWordByName(
                        GlobalData.getInstance().getAllWord(), similarWord.getName()
                );
                if (oneWordByName != null) {
                    List<Word.WordMeaning> meaningList = oneWordByName.getMeaningList();
                    similarWord.setAnotation(UIUtil.meaningList2String(meaningList));
                    inputChildWord = inputChildWord.replaceAll("\\b" + similarWord.getName() + "\\b", similarWord.getName()
                            + "  " + similarWord.getAnotation());
                    hasModify = true;
                }
            }
        }

        // 在去除重复的
        searchWordList.removeAll(exitSimilar);
        if (searchWordList.isEmpty() && !hasModify)
            return  inputChildWord;

        if (!mIsInEdit) {
            switchEdit();
        }
        return  UIUtil.joinSimilar(inputChildWord, new ArrayList<>(searchWordList));
    }

    @Override
    public void saveInputAssistant() {
        String inputName = mView.getInputWordName();
        if (null == inputName || inputName.isEmpty()) {
            SpUtil.saveInputAssistantKey("");
        } else {
            SpUtil.saveInputAssistantKey(mWord.getName());
        }
        Toast.makeText(mContext, "输入填写保存成功", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean isRefreshList() {
        return isRefreshList;
    }

}
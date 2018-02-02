package com.lgc.memorynote.wordList;

import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.wordDetail.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 小命令行系统中的命令
 * 目前的命令分两种，一种是搜索的，可以是单词，短语，汉语意思
 * 另一种是规定好的命令，以-开头，
 */
public class Command {
    /************************* 排序相关的命令***************************/
    /** 按不熟悉度 */
    public static final String _stra  = "-stra";
    /** 按最后记忆时间 */
    public static final String _last  = "-last";
    /** 按字典序 */
    public static final String _dict  = "-dict";
    /** 按长度 */
    public static final String _len   = "-len";


    /** 反向排序 */
    public static final String _rev   = "-rev";



    /*********************** 过滤相关 *******************************/
    /** 只显示单词 */
    public static final String _word   = "-word";
    /** 只显示短语 */
    public static final String _phr    = "-phr";
    /** 只显示生的单词 */
    public static final String _sheng =  "-" + Word.WordMeaning.TAG_SHENG;
    /** 只显示词义怪的词 */
    public static final String _guai   =  "-" + Word.WordMeaning.TAG_GUAI;
    /** 把近似的词放到一起 */
    public static final String _sim    = "-sim";
    private List<Word> resultList;

    public static List<String> commadList = Arrays.asList(_stra, _last, _dict, _len, _rev, _word, _phr, _sheng, _guai, _sim);

    /**
     * 解析输入的命令
     */
    public static List<String> analysisCommandInput(String inputCommand) {
        List<String> commandList = new ArrayList<>();
        if (inputCommand == null) return commandList;
        inputCommand = inputCommand.trim();
        if (inputCommand.isEmpty()) return commandList;
        String[] temp = inputCommand.split(" ");
        for (String one : temp) {
            one = one.trim();
            if (!one.isEmpty()) {

            }
        }
        return commandList;
    }

    /**
     * 排序，会生成一个新的列表，不改变原来的数据
     * @param commandList 必须是标准的命令才有效
     */
    public static List<Word> orderByCommand(List<String> commandList, List<Word> wordList) {
        List<Word> resultList = new ArrayList<>(wordList.size());
        resultList.addAll(wordList);

        // 第一步，如果是搜索，搜索出满足条件的
        for (int i = commandList.size() - 1; i >= 0; i--) {
            String search = commandList.get(i);
            if (!search.startsWith("-")) {
                SearchUtil.searchWord(search, resultList);
                commandList.remove(i);
            }
        }
        // 第二步，进行过滤操作
        for (String grep : commandList) {
            if (_phr.equals(grep)) {
                SearchUtil.grepNotPhrase(wordList);
            } else if (_word.equals(grep)) {
                SearchUtil.grepNotWord(wordList);
            } else if (_sheng.equals(grep) || (_guai.equals(grep))) {
                SearchUtil.grepNoTag(_guai.substring(1, _guai.length()),wordList);
            }
        }
        return wordList;
    }


    public void showWordMeaning(List<Word.WordMeaning> wordMeaningList) {
        // 解析词义，特殊的词意采用特殊的颜色
        String lastCixing = Word.WordMeaning.CIXING_N;
        for (Word.WordMeaning oneMeaning : wordMeaningList) {
            if (!lastCixing.equals(oneMeaning.getCiXing())) {
                lastCixing = oneMeaning.getCiXing();
            }
            if (oneMeaning.isGuai() && oneMeaning.isSheng()) {
//                mTvWordMeaning.setText(wordMeaningList);
            } else if (oneMeaning.isGuai() || oneMeaning.isSheng()) {

            }
        }
    }
}

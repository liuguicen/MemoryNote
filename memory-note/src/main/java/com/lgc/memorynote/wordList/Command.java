package com.lgc.memorynote.wordList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
    public static final String _shen   = "-shen";
    /** 只显示词义怪的词 */
    public static final String _guai   = "-guai";
    /** 把近似的词放到一起 */
    public static final String _sim    = "-sim";
    private List<Word> resultList;


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
        Collections.copy(resultList, wordList);

        // 第一步，如果是搜索，搜索出满足条件的
        for (int i = commandList.size() - 1; i >= 0; i--) {
            String search = commandList.get(i);
            if (!search.startsWith("-")) {
                searchWord(search, resultList);
                commandList.remove(i);
            }
        }
        // 第二步，进行过滤操作
        for (String grep : commandList) {
            if (_phr.equals(grep)) {
                grepNotPhrase(wordList);
            } else if (_word.equals(grep)) {
                grepNotWord(wordList);
            } else if (_shen.equals(grep) || (_guai.equals(grep))) {
                grepNoTag(_guai.substring(1, _guai.length()),wordList);
            }
        }
        return wordList;
    }

    private static void grepNoTag(String tag, List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            Word word = wordList.get(i);
            List<Word.WordMeaning> meaningList = word.getMeaningList();
            int j = 0;
            for (j = 0; j < meaningList.size(); j++) {
                if (meaningList.get(i).hasTag(tag)) {
                    break;
                }
            }
            if (j > meaningList.size()) {
                wordList.remove(i);
            }
        }
    }

    private static void grepNotPhrase(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (!wordList.get(i).isPhrase()) {
                wordList.remove(i);
            }
        }
    }


    private static void grepNotWord(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (wordList.get(i).isPhrase()) {
                wordList.remove(i);
            }
        }
    }

    /**
     * 会搜索所有包含关键字的项，完全匹配的放在最前面
     * @param search
     * @param wordList
     * @return
     */
    private static List<Word> searchWord(String search, List<Word> wordList) {
        // 含有非单词字符，搜索词义
        if (Pattern.compile("[^a-zA-Z\\-]").matcher(search).find()) {
            for (int i = wordList.size() -1; i >= 0; i--) {
                Word word = wordList.get(i);
                List<Word.WordMeaning> meaningList = word.getMeaningList();
                if (meaningList == null) {
                    wordList.remove(i);
                    continue;
                }

                int j = 0;
                for (; j < meaningList.size(); j++){
                    String meaning = meaningList.get(j).getMeaning();
                    if (meaning != null && meaning.contains(search)) {
                        if (meaning.equals(search)) {
                            wordList.remove(i);
                            wordList.add(0, word);
                        }
                        break;
                    }
                }
                if (j >= wordList.size()) {
                    wordList.remove(i);
                }

            }
        } else {
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                String name = word.getWord();
                if (name != null && name.contains(search)) {
                    if (name.equals(search)) {
                        wordList.remove(i);
                        wordList.add(0, word);
                    }
                } else {
                    wordList.remove(i);
                }
            }
        }
        return wordList;
    }


    public void showWordMeaning(List<Word.WordMeaning> wordMeaningList) {
        // 解析词义，特殊的词意采用特殊的颜色
        String lastCixing = Word.WordMeaning.MEANING_N;
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

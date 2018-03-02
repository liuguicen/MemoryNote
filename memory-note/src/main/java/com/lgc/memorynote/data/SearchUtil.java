package com.lgc.memorynote.data;

import android.text.TextUtils;

import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.wordList.Command;

import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/02
 *      version : 1.0
 * <pre>
 */

public class SearchUtil {
    public static void grepNoTag(String tag, List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            Word word = wordList.get(i);
            List<Word.WordMeaning> meaningList = word.getMeaningList();
            if (meaningList == null) return;
            int j = 0;
            for (j = 0; j < meaningList.size(); j++) {
                if (meaningList.get(j).hasTag(tag)) {
                    break;
                }
            }
            if (j >= meaningList.size()) {
                wordList.remove(i);
            }
        }
    }

    public static void grepNotPhrase(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (!wordList.get(i).getName().contains(" ")) {
                wordList.remove(i);
            }
        }
    }

    public static void grepNotWord(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (wordList.get(i).getName().contains(" ")) {
                wordList.remove(i);
            }
        }
    }

    /**
     * 会搜索所有包含关键字的项，完全匹配的放在最前面
     *
     * @param search
     * @param wordList
     * @return
     */
    public static List<Word> searchWordOrMeaning(String search, List<Word> wordList) {
        // 含有非单词字符，搜索词义
        if (TextUtils.isEmpty(search)) return wordList;
        List<Word> searchedList = new ArrayList<>();
        if (search.startsWith(Command.COMMAND_START + Command.REGEX_SERACH)) {
            String regex = search.substring(Command.COMMAND_START.length() +
                    Command.REGEX_SERACH.length(), search.length()).trim();
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                String name = word.getName();
                if (name != null && name.matches(regex)) {
                    searchedList.add(word);
                }
            }
        } else if (Pattern.compile(Word.NOT_NAME_FORMAT_REGEX).matcher(search).find()) {
            int equalEnd = 0;
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                int res = word.containMeaning(search);
                if (res == 2) {
                    searchedList.add(0, word);
                    equalEnd++;
                } else if (res == 1) {
                    searchedList.add(equalEnd, word);
                } else if (res == 0){
                    searchedList.add(word);
                }
            }
        } else {
            int equalNumber = 0;
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                String name = word.getName();
                if (name != null && name.contains(search)) {
                    if (name.equals(search)) {
                        searchedList.add(0, word);
                        equalNumber++;
                    } else if (name.startsWith(search)) {
                        searchedList.add(equalNumber, word);
                    } else {
                        searchedList.add(word);
                    }
                }
            }
        }
        return searchedList;
    }

    /**
     * @param name name传入合法的最好
     */
    public Word searchWordByName(List<Word> wordList, String name) {
        if (wordList == null || name == null) return null;
        for (Word w : wordList) {
            if (name.equals(w.getName())) {
                return w;
            }
        }
        return null;
    }


    public static Word getOneWordByName(List<Word> wordList, String name) {
        if (wordList != null) {
            for (Word word : wordList) {
                if (TextUtils.equals(word.getName(), name))
                    return word;
            }
        }
        return null;
    }

    /**
     * 如果该单词的相似单词列表包含了这个单词，就将整个相似列表添加进来
     * @param name 同步列表的单词的名字
     */
    public static Set<Word.SimilarWord> searchAllSimilars(List<Word> wordList, String name) {
        Map<String,Word.SimilarWord> resultSimilarMap = new LinkedHashMap<>();
        for (Word word : wordList) {
            addSimilarList(name, resultSimilarMap, word.getSimilarWordList(), word);
        }

        resultSimilarMap.remove(name);
        return Util.map2set(resultSimilarMap);
    }


    /**
     * 如果该单词的相似单词列表包含了这个单词，就将整个词组列表添加进来
     */
    public static Set<Word.SimilarWord> searchAllGroups(List<Word> wordList, String name) {
        Map<String, Word.SimilarWord> resultGroupMap = new LinkedHashMap<>();
        for (Word word : wordList) {
            addSimilarList(name, resultGroupMap, word.getGroupList(), word);
        }

        resultGroupMap.remove(name);
        return Util.map2set(resultGroupMap);
    }

    private static void addSimilarList(String srcName, Map<String, Word.SimilarWord> resultSimilarMap, List<Word.SimilarWord> srcSimilarList, Word matchWord) {
        if (srcSimilarList == null) return;

        for (Word.SimilarWord similarWord : srcSimilarList) {
            // 如果该单词的相似单词列表包含了这个单词，就将整个相似列表添加进来
            if (TextUtils.equals(similarWord.getName(), srcName)) {
                for (int j= 0; j< srcSimilarList.size(); j ++) {
                    Word.SimilarWord addSimilar = srcSimilarList.get(j);
                    resultSimilarMap.put(addSimilar.getName(), addSimilar);
                }

                // do not contain the word self should add it
                if (!resultSimilarMap.containsKey(matchWord.getName())) {
                    // convert word to similar word and use wordMeaning which be replace "\n" to " " to create anotation
                    Word.SimilarWord selfSimilar = new Word.SimilarWord();
                    selfSimilar.setName(matchWord.getName());
                    selfSimilar.setAnotation(matchWord.getInputMeaning().replace("\n", " "));
                    resultSimilarMap.put(selfSimilar.getName(), selfSimilar);
                }
                return;
            }
        }
    }
}

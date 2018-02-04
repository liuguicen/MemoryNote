package com.lgc.memorynote.data;

import android.text.TextUtils;

import com.lgc.memorynote.wordDetail.Word;

import java.util.List;
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

    public static void grepNotPhrase(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
//            if (!wordList.get(i).isPhrase()) {
                wordList.remove(i);
//            }
        }
    }

    public static void grepNotWord(List<Word> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
//            if (wordList.get(i).isPhrase()) {
//                wordList.remove(i);
//            }
        }
    }

    /**
     * 会搜索所有包含关键字的项，完全匹配的放在最前面
     * @param search
     * @param wordList
     * @return
     */
    public static List<Word> searchWordOrMeaning(String search, List<Word> wordList) {
        // 含有非单词字符，搜索词义
        if (search == null) return wordList;
        if (Pattern.compile(Word.NAME_FORMAT_REGEX).matcher(search).find()) {
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
                String name = word.getName();
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
}

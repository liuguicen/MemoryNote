package com.lgc.memorynote.data;

import android.text.TextUtils;
import android.util.Pair;

import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.wordList.Command;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/02
 *      version : 1.0
 * <pre>
 */

public class SearchUtil {
    public static void grepNoTag(String tag, List<WordWithComparator> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            IWord word = wordList.get(i);
            if (!word.hasTag(tag)) {
                wordList.remove(i);
            }
        }
    }

    public static void grepNotPhrase(List<WordWithComparator> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (!wordList.get(i).getName().contains(" ")) {
                wordList.remove(i);
            }
        }
    }

    public static void grepNotWord(List<WordWithComparator> wordList) {
        for (int i = wordList.size() - 1; i >= 0; i--) {
            if (!WordUtil.isWord(wordList.get(i).getName())) {
                wordList.remove(i);
            }
        }
    }

    /**
     * 各种类型的搜索，有点多
     *
     * @param search
     * @param wordList 不会改动此数组
     * @return
     */
    public static boolean searchMultiAspects(String search, List<Word> wordList, List<WordWithComparator> searchedList) throws NumberFormatException{
        // 含有非单词字符，搜索词义
        if (searchedList == null) return true;
        if (TextUtils.isEmpty(search)) {
            for (Word word : wordList) {
                searchedList.add(new WordWithComparator(word));
            }
            return true;
        }

        if (search.contains(Command._strange_degree)) {  // 按陌生度过滤
            String sd = search.substring(Command._strange_degree.length()).trim();
            // 赋极值
            int bigger = Integer.MIN_VALUE, smaller = Integer.MAX_VALUE, equal = Integer.MAX_VALUE;
            Matcher smallMatcher = Pattern.compile(">[ ]*(\\d+)").matcher(sd);
            if (smallMatcher.find()) {
                smaller = Integer.valueOf(smallMatcher.group(1));
            }

            Matcher bigMatcher = Pattern.compile("<[ ]*(\\d+)").matcher(sd);
            if (bigMatcher.find()) {
                bigger = Integer.valueOf(bigMatcher.group(1));
            }

            if (bigger == Integer.MIN_VALUE && smaller == Integer.MAX_VALUE) {
                Matcher equalMatcher = Pattern.compile("\\d+").matcher(sd);
                if (equalMatcher.find()) {
                    equal = Integer.valueOf(equalMatcher.group());
                } else {
                    throw new NumberFormatException("比较格式错误");
                }
            }

            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                int innerSd = word.getStrangeDegree();
                if (equal == innerSd || (innerSd < bigger && innerSd > smaller)) {
                    WordWithComparator wordWithComparator = new WordWithComparator(word);
                    wordWithComparator.addComparator(innerSd * -1); // 倒序
                    searchedList.add(wordWithComparator);
                }
            }
        } else if (search.startsWith(Command._regex_search)) {  // 正则式搜索
            int lastId = Command._regex_search.length();

            boolean global = false;
            int globalId = search.indexOf(Command._global);
            if (globalId >= lastId) {
                global = true;
                lastId = globalId + Command._global.length();
            }
            if (globalId < 0) globalId = search.length();

            String regex = search.substring(Command._regex_search.length(), globalId).trim();
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                String data;
                if (!global) data = word.getName();
                else data = word.toString();

                if (data != null && data.matches(regex)) {
                    searchedList.add(new WordWithComparator(word));
                }
            }
        } else if (search.startsWith(Command.TAG_START)) { // 搜索Tag
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                if (word.hasTag(search)) {
                    searchedList.add(new WordWithComparator(word));
                }
            }
        } else if (search.startsWith(Command._time_to_now)) {
            String sdays = search.substring(Command._strange_degree.length()).trim();
            int days = Integer.valueOf(sdays);
            long oneDayMil = 24 * 3600 * 1000;
            long curDays = System.currentTimeMillis() / oneDayMil + 1;
            long startTime = (curDays - days) * oneDayMil;
            for (int i = wordList.size() - 1; i >= 0; i--) {
                Word word = wordList.get(i);
                if (word.getLastRememberTime() > startTime) {
                    WordWithComparator wordWithComparator = new WordWithComparator(word);
                    wordWithComparator.addComparator(word.getLastRememberTime());
                    searchedList.add(wordWithComparator);
                }
            }
        } else { // 搜索所有内容，按下面这个顺序依次搜索，并按搜索结果排序
            for (Word word : wordList) {
                double matchDegree = word.matchDegree(search);
                WordWithComparator wordWithComparator = new WordWithComparator(word);
                wordWithComparator.addComparator(matchDegree * -1); // 相似度高排前面，需要倒序
                searchedList.add(wordWithComparator);
            }
        }
        return true;
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
     *
     * @param name 同步列表的单词的名字
     */
    public static Set<Word.SimilarWord> searchAllSimilars(List<Word> wordList, String name) {
        Map<String, Word.SimilarWord> resultSimilarMap = new LinkedHashMap<>();
        for (Word word : wordList) {
            addSimilarList(name, resultSimilarMap, word.getSimilarWordList(), word);
        }

        resultSimilarMap.remove(name);
        return Util.map2set(resultSimilarMap);
    }


    /**
     * 如果该单词的词组列表包含了这个单词，就将整个词组列表添加进来
     */
    public static Set<Word.SimilarWord> searchAllGroups(List<Word> wordList, Word srcWord, String srcName) {
        Map<String, Word.SimilarWord> resultGroupMap = new LinkedHashMap<>();

        // 查找词根词缀的词组
        int wordType = WordUtil.getWordType(srcWord);
        if (wordType == Word.ROOT || wordType == Word.PREFIX || wordType == Word.SUFFIX) {
            List<String> rootAffixList;
            rootAffixList = UIUtil.getRootAffixList(srcName);

            for (String one : rootAffixList) {
                for (Word word : wordList) {
                    if (word.getName().contains(one)) {
                        // convert word to similar word and use wordMeaning which be replace "\n" to " " to create anotation
                        Word.SimilarWord selfSimilar = new Word.SimilarWord();
                        selfSimilar.setName(word.getName());
                        selfSimilar.setAnotation(word.getInputMeaning().replace("\n", " "));
                        resultGroupMap.put(selfSimilar.getName(), selfSimilar);
                    }
                }
            }
        } else {
            String name = srcName;
            for (Word word : wordList) {
                addSimilarList(name, resultGroupMap, word.getGroupList(), word);
            }
        }

        resultGroupMap.remove(srcName);
        return Util.map2set(resultGroupMap);
    }

    private static void addSimilarList(String srcName, Map<String, Word.SimilarWord> resultSimilarMap,
                                       List<Word.SimilarWord> srcSimilarList, Word matchWord) {
        if (srcSimilarList == null) return;

        for (Word.SimilarWord similarWord : srcSimilarList) {
            // 如果该单词的相似单词列表包含了这个单词，就将整个相似列表添加进来
            if (TextUtils.equals(similarWord.getName(), srcName)) {
                for (int j = 0; j < srcSimilarList.size(); j++) {
                    Word.SimilarWord addSimilar = srcSimilarList.get(j);
                    resultSimilarMap.put(addSimilar.getName(), addSimilar);
                }

                // do not contain the word self,  should add it
                if (!resultSimilarMap.containsKey(matchWord.getName())
                        && WordUtil.getWordType(matchWord) == Word.NORMAL) {
                    // convert word to similar word and use wordMeaning which be replace "\n" to " " to create anotation
                    Word.SimilarWord selfSimilar = new Word.SimilarWord();
                    selfSimilar.setName(matchWord.getName());
                    selfSimilar.setAnotation(matchWord.getInputMeaning().replace("\n", " "));
                    resultSimilarMap.put(selfSimilar.getName(), selfSimilar);
                }
                return;
            }
        }
        // 没有找到，考虑单词是否互为子单词
        if (srcName.length() != matchWord.getName().length() && WordUtil.getWordType(matchWord) == Word.NORMAL
                && srcName.contains(matchWord.getName()) || matchWord.getName().contains(srcName)) {
            // do not contain the word self,  should add it
            if (!resultSimilarMap.containsKey(matchWord.getName())
                    && WordUtil.getWordType(matchWord) == Word.NORMAL) {
                // convert word to similar word and use wordMeaning which be replace "\n" to " " to create anotation
                Word.SimilarWord selfSimilar = new Word.SimilarWord();
                selfSimilar.setName(matchWord.getName());
                selfSimilar.setAnotation(matchWord.getInputMeaning().replace("\n", " "));
                resultSimilarMap.put(selfSimilar.getName(), selfSimilar);
            }
        }
    }

    public static ArrayList<Pair<Integer, String>> searchRootAffix(List<Word> allWord, String name) {
        ArrayList<Pair<Integer, String>> matchList = new ArrayList<>();
        if (allWord == null || name == null) return matchList;
        for (Word word : allWord) {
            int wordType = WordUtil.getWordType(word);
            if (wordType == Word.ROOT || wordType == Word.PREFIX || wordType == Word.SUFFIX) {

                List<String> rootAffixList = UIUtil.getRootAffixList(word.getName());
                for (String oneRA : rootAffixList) {
                    if ((wordType == Word.ROOT && name.contains(oneRA))
                            || (wordType == Word.PREFIX && name.startsWith(oneRA))
                            || (wordType == Word.SUFFIX && name.endsWith(oneRA))) {
                        String ram = oneRA + "  " + Util.convertToString(word.getMeaningList());
                        matchList.add(new Pair<>(wordType, ram));
                    }
                }
            }
        }
        return matchList;
    }
}

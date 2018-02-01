package com.lgc.memorynote.wordDetail;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单词数据的解析器
 */
public class WordAnalyzer {
    /**
     * 解析成功
     */
    public static final int SUCCESS = 1;
    public static final int IS_NULL = 2;
    public static final int TAG_FORMAT_ERROR = 3;
    public static final int NO_VALID_MEANING = 4;

    public static class AnalyzeResult{
        int resultCode;
        String msg;
        public AnalyzeResult(int resultCode, String msg) {
            this.resultCode = resultCode;
            this.msg = msg;
        }
    }

    /**
     * 解析用户输入的单词的意思的数据
     * @return 解析结果状态码
     */
    public static int analyzeMeaningFromUser(String originalMeaning, List<Word.WordMeaning> meaningList) {
        if (originalMeaning == null || meaningList == null) return WordAnalyzer.IS_NULL;
        originalMeaning = originalMeaning.trim();
        if (originalMeaning.isEmpty()) return IS_NULL;
        int resultCode = WordAnalyzer.SUCCESS;
        /**
         * 惰性匹配，并且匹配换行
         */
        Matcher matcher = Pattern.compile("(@.*?|.*?)(#.*?#)", Pattern.DOTALL).matcher(originalMeaning);
        while(matcher.find()) {
            Word.WordMeaning oneMeaning = new Word.WordMeaning();

            // 处理tag相关的
            Matcher tagMather = Pattern.compile("@[^@#]*", Pattern.DOTALL).matcher(matcher.group(1));
            while (tagMather.find()) {
                String tag = tagMather.group(0).trim();
                if(Word.WordMeaning.MEANING_GUAI.equals(tag)) {
                    oneMeaning.setGuai(true);
                } if (Word.WordMeaning.MEANING_SHENG.equals(tag)) {
                    oneMeaning.setSheng(true);
                } else if (tag.length() > 1){
                    boolean isValid = oneMeaning.setCiXing(tag.substring(1, tag.length()).trim());
                    if (!isValid) {
                        resultCode = WordAnalyzer.TAG_FORMAT_ERROR;
                    }
                }
            }

            // 处理实际意思
            String realMeaning = matcher.group(2);
            if (realMeaning != null && realMeaning.length() >= 2) {
                realMeaning = realMeaning.substring(1, realMeaning.length() -1).trim();
            }
            if (realMeaning == null || realMeaning.trim().isEmpty()) { // 意思为空
                continue;
            }
            oneMeaning.setMeaning(realMeaning);
            meaningList.add(oneMeaning);
        }
        // 对于没加任何标签的，直接解析，简化用户输入
        if (!originalMeaning.isEmpty() && !originalMeaning.contains("#") && !originalMeaning.contains("@")) {
            Word.WordMeaning oneMeaning = new Word.WordMeaning();
            oneMeaning.setMeaning(originalMeaning);
            meaningList.add(oneMeaning);
        }
        if (meaningList.size() == 0) { // 没有获取到有效的词义，不设置数据
            resultCode = WordAnalyzer.NO_VALID_MEANING;
        }
        return resultCode;
    }

    /**
     * 解析用户输入的相似单词
     */
    public static int analyzeSimilarWordsFromUser(String inputSimilarWord, List<String> similarWordList) {
        if (inputSimilarWord == null || similarWordList == null) return IS_NULL;
        inputSimilarWord = inputSimilarWord.trim();
        if(inputSimilarWord.isEmpty()) return IS_NULL;
        String[] similarWords = inputSimilarWord.split(" ");
        for (String oneWord : similarWords) {
            //if (oneWord.matches("[^a-zA-z\\-]")) {// 先不限制用户输入
            //
            //}
            oneWord = oneWord.trim();
            if (!oneWord.isEmpty()) {
                similarWordList.add(oneWord);
            }
        }
        return SUCCESS;
    }

}

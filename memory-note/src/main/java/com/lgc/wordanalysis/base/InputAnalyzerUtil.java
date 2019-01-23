package com.lgc.wordanalysis.base;

import com.lgc.wordanalysis.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lgc.wordanalysis.data.Word.WordMeaning;

/**
 * 单词数据的解析器
 */
public class InputAnalyzerUtil {
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
     * 最后会按词性排序
     * @return 解析结果状态码
     */
    public static int analyzeInputMeaning(String originalMeaning, Word word) {
        if (originalMeaning == null || word == null) return InputAnalyzerUtil.IS_NULL;
        originalMeaning = originalMeaning.trim();
        if (originalMeaning.isEmpty()) return IS_NULL;
        int resultCode = InputAnalyzerUtil.SUCCESS;

        /**
         * one line one meaning
         */
        String[] oneArray = originalMeaning.split("\n");
        List<String> tagList = new ArrayList<>();
        List<WordMeaning> meaningList = new ArrayList<>();
        for(String one : oneArray) { // 找到了，解析tag和词义
            if (one == null || one.isEmpty()) continue;

            // first, 处理tag相关的
            one = one.trim();
            int tagEnd = 0;
            if (one.startsWith(Word.TAG_START)) {
                Matcher tagMather = Pattern.compile("@.+?\\s").matcher(one);
                while (tagMather.find()) {
                    tagEnd = tagMather.end();
                    String tag = tagMather.group().trim();
                    if (tag.length() <= Word.TAG_START.length()) continue;
                    tagList.add(tag);
                }
            }

            // second handle the meaning, if the meaning is null, the tag is valid\
            WordMeaning oneMeaning = new WordMeaning();
            if (tagEnd >= 1) tagEnd--; // real position
            if (tagEnd >= one.length())
                continue;
            String tempMeaning = one.substring(tagEnd, one.length()).trim();
            analysisNoTagMeaning(oneMeaning, tempMeaning);
            meaningList.add(oneMeaning);
        }
        word.setTagList(tagList);
        word.setMeaningList(meaningList);

        if (word.getMeaningList() == null || word.getMeaningList().size() == 0) { // 没有获取到有效的词义，不设置数据
            resultCode = InputAnalyzerUtil.NO_VALID_MEANING;
        }
        return resultCode;
    }

    public static int analysisNoTagMeaning(WordMeaning oneMeaning, String tempMeaning) {
        if (tempMeaning == null) {
            return NO_VALID_MEANING;
        } else {
            tempMeaning = tempMeaning.trim();
            if (tempMeaning.isEmpty()) return NO_VALID_MEANING;
            // don't match any cixing, it's the meaning
            oneMeaning.setMeaning(tempMeaning);
        }
        return SUCCESS;
    }

    private static int setRealMeaning(WordMeaning oneMeaning, String tempMeaning, String cixing) {
        if (cixing.length() >= tempMeaning.length()) {
            return NO_VALID_MEANING;
        }
        String realMeaning = tempMeaning.substring(cixing.length(), tempMeaning.length()).trim();
        oneMeaning.putValidCiXing(cixing);
        oneMeaning.setMeaning(realMeaning);
        return SUCCESS;
    }

    /**
     * 解析用户输入的相似单词
     */
    public static int analyzeInputSimilarWords(String inputSimilarWord, List<Word.SimilarWord> similarWordList) {
        if (inputSimilarWord == null || similarWordList == null) return IS_NULL;
        inputSimilarWord = inputSimilarWord.trim();
        if(inputSimilarWord.isEmpty()) return IS_NULL;
        String[] similarWords = inputSimilarWord.split("\n");
        for (String oneWord : similarWords) {
            //if (oneWord.matches(Word.NOT_NAME_PHRASE_REGEX)) {// 先不限制用户输入
            //
            //}
            oneWord = oneWord.trim();
            if (oneWord.isEmpty()) continue;
            Word.SimilarWord similarWord = new Word.SimilarWord();
            Matcher matcher = Pattern.compile(Word.NOT_NAME_PHRASE_REGEX).matcher(oneWord);
            int annoStart = -1;
            if (matcher.find())
                annoStart = matcher.start();
            String cixingRegex = " +("  + WordMeaning.CIXING_ADJ + "|"
                                       + WordMeaning.CIXING_ADV + "|"
                                       + WordMeaning.CIXING_N   + "|"
                                       + WordMeaning.CIXING_V
                               + ") *[^a-zA-z\\-']";
            Matcher cixingMatcher = Pattern.compile(cixingRegex).matcher(oneWord);
            if (cixingMatcher.find()) {
                annoStart = Math.min(annoStart, cixingMatcher.start());
            }

            if (annoStart >= 0) {
                String annotation = oneWord.substring(annoStart, oneWord.length());
                similarWord.setAnotation(annotation);
            }
            String name = oneWord.substring(0,  annoStart == -1 ? oneWord.length() : annoStart).trim();
            if (!oneWord.isEmpty()) {
                similarWord.setName(name);
                similarWordList.add(similarWord);
            }
        }
        return SUCCESS;
    }
}

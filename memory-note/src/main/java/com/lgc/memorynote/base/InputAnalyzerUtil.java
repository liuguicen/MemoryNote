package com.lgc.memorynote.base;

import com.lgc.memorynote.wordDetail.Word;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lgc.memorynote.wordDetail.Word.WordMeaning;

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
     * analyze cmd, will change it and return it
     * @param inputCmd
     * @param commandList
     * @return
     */
    public static String analyzeInputCommand(String inputCmd, List<String> commandList) {
        if (inputCmd == null || commandList == null) return inputCmd;
        inputCmd = inputCmd.trim();
        if(inputCmd.isEmpty()) return inputCmd;

        int tagEnd = 0;
        if (inputCmd.startsWith(WordMeaning.TAG_START)) { // has tag cmd
            Matcher tagMather = Pattern.compile("@.+?\\s").matcher(inputCmd);
            while (tagMather.find()) {
                tagEnd = tagMather.end();
                String tag = tagMather.group().trim();
                if (tag.length() <= WordMeaning.TAG_START.length()) continue;
                commandList.add(tag);
            }
        }

        // second handle the meaning, if the meaning is null, the tag is valid\
        if (tagEnd >= 1) tagEnd--;
        if (tagEnd >= inputCmd.length())
            return "";
        String search = inputCmd.substring(tagEnd, inputCmd.length()).trim();
        return search;
    }


    /**
     * 解析用户输入的单词的意思的数据
     * 最后会按词性排序
     * @return 解析结果状态码
     */
    public static int analyzeInputMeaning(String originalMeaning, List<WordMeaning> meaningList) {
        if (originalMeaning == null || meaningList == null) return InputAnalyzerUtil.IS_NULL;
        originalMeaning = originalMeaning.trim();
        if (originalMeaning.isEmpty()) return IS_NULL;
        int resultCode = InputAnalyzerUtil.SUCCESS;

        /**
         * one line one meaning
         */
        String[] oneArray = originalMeaning.split("\n");
        for(String one : oneArray) { // 找到了，解析tag和词义
            if (one == null || one.isEmpty()) continue;
            WordMeaning oneMeaning = new WordMeaning();

            // first, 处理tag相关的
            one = one.trim();
            int tagEnd = 0;
            if (one.startsWith(WordMeaning.TAG_START)) {
                Matcher tagMather = Pattern.compile("@.+?\\s").matcher(one);
                while (tagMather.find()) {
                    tagEnd = tagMather.end();
                    String tag = tagMather.group().trim();
                    if (tag.length() <= WordMeaning.TAG_START.length()) continue;
                    oneMeaning.addValidTag(tag);
                }
            }

            // second handle the meaning, if the meaning is null, the tag is valid\
            if (tagEnd >= 1) tagEnd--; // real position
            if (tagEnd >= one.length())
                continue;
            String tempMeaning = one.substring(tagEnd, one.length()).trim();
            analysisNoTagMeaning(oneMeaning, tempMeaning);
            meaningList.add(oneMeaning);
        }

        if (meaningList.size() == 0) { // 没有获取到有效的词义，不设置数据
            resultCode = InputAnalyzerUtil.NO_VALID_MEANING;
        } else {
            Collections.sort(meaningList, new Comparator<WordMeaning>() {
                @Override
                public int compare(WordMeaning o1, WordMeaning o2) {
                    if (o1 == null && o2 == null) return 0;
                    if (o1 == null) return 1;
                    if (o2 == null) return -1;
                    int x1 = WordMeaning.getCiXingImportance(o1.getCiXing());
                    int x2 = WordMeaning.getCiXingImportance(o2.getCiXing());
                    //  值越小，越重要，故直接从小到大排
                    return x1 - x2;
                }
            });
        }
        return resultCode;
    }

    public static int analysisNoTagMeaning(WordMeaning oneMeaning, String tempMeaning) {
        if (tempMeaning == null) {
            return NO_VALID_MEANING;
        } else {
            tempMeaning = tempMeaning.trim();
            if (tempMeaning.isEmpty()) return NO_VALID_MEANING;
            if (tempMeaning.startsWith(WordMeaning.CIXING_N)) {
                if (SUCCESS != setRealMeaning(oneMeaning, tempMeaning,
                        WordMeaning.CIXING_N))
                    return NO_VALID_MEANING;
            } else if (tempMeaning.startsWith(WordMeaning.CIXING_V)) {
                if (SUCCESS != setRealMeaning(oneMeaning, tempMeaning,
                        WordMeaning.CIXING_V))
                    return NO_VALID_MEANING;
            } else if (tempMeaning.startsWith(WordMeaning.CIXING_ADJ)) {
                if (SUCCESS != setRealMeaning(oneMeaning, tempMeaning,
                        WordMeaning.CIXING_ADJ))
                    return NO_VALID_MEANING;
            } else if (tempMeaning.startsWith(WordMeaning.CIXING_ADV)) {
                if (SUCCESS != setRealMeaning(oneMeaning, tempMeaning,
                        WordMeaning.CIXING_ADV))
                    return NO_VALID_MEANING;
            } else { // don't match any cixing, it's the meaning
                oneMeaning.setMeaning(tempMeaning);
            }

        }
        return SUCCESS;
    }

    private static int setRealMeaning(WordMeaning oneMeaning, String tempMeaning, String cixing) {
        if (cixing.length() >= tempMeaning.length()) {
            return NO_VALID_MEANING;
        }
        String realMeaning = tempMeaning.substring(cixing.length(), tempMeaning.length()).trim();
        oneMeaning.setValidCiXing(cixing);
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
            //if (oneWord.matches(Word.NOT_NAME_FORMAT_REGEX)) {// 先不限制用户输入
            //
            //}
            oneWord = oneWord.trim();
            Word.SimilarWord similarWord = new Word.SimilarWord();
            int annoStart = oneWord.indexOf("//");
            if (annoStart >= 0) {
                String annotation = oneWord.substring(annoStart + 2, oneWord.length());
                similarWord.setAnotation(annotation);
            }
            String name = oneWord.substring(0,  annoStart != -1 ? annoStart : oneWord.length()).trim();
            if (!oneWord.isEmpty()) {
                similarWord.setName(name);
                similarWordList.add(similarWord);
            }
        }
        return SUCCESS;
    }
}

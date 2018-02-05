package com.lgc.memorynote;

import com.google.gson.Gson;
import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.wordDetail.Word;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import  static  com.lgc.memorynote.wordDetail.Word.WordMeaning;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        testWordInput();
        testAnalyzeSimilarWord();
//        testInputWordTag();
//        textInputNoTagMeaning();
//        textInputMeanings(new Word());
//        testInputName();
    }

    public void testInputName() {
        String inputName = "sdfsdf-sdfs' bn/dfsd";
        System.out.println(Pattern.compile(Word.NOT_NAME_FORMAT_REGEX).matcher(inputName).find());
    }

    private void testWordInput() {
        Word word = new Word();

        testInputSimilar(word);
        textInputMeanings(word);
        System.out.println(new Gson().toJson(word));
    }

    private void textInputMeanings(Word word) {
        String input = "@sheng n. 相信，信任\n" +
                " * @guai @sheng v. 赞颂，把...归功于\n" +
                "   n.信任，学分，声望\n\n"
                + "adj.国会的，议会的\n\n"
                + "@guai @gsdf   adv. √▆▃♠▆";
        List<WordMeaning> wordMeaningList = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputMeaning(input, wordMeaningList);
        System.out.println(new Gson().toJson(wordMeaningList));
        word.setInputMeaning(input);
        word.setMeaningList(wordMeaningList);
    }

    public void testInputSimilar(Word word) {
        String input = "sdfsd word id good // 哄哄的东方盛世房东发的" +
                "hahaha// 双方双方双方爽肤水";
        List<Word.SimilarWord> similarList = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputSimilarWords(input, similarList);
        System.out.println(similarList);
        word.setSimilarWordList(similarList);
    }

    private void textInputNoTagMeaning() {
        WordMeaning wordMeaning = new WordMeaning();
        InputAnalyzerUtil.analysisNoTagMeaning(wordMeaning, "n .   中国,rt, 日本 ");
        System.out.println(new Gson().toJson(wordMeaning));
    }

    private void testAnalyzeSimilarWord() {
        String similarWord =  "sdfsd word id good // 哄哄的东方盛世房东发的\n" +
                "hahaha// 双方双方双方爽肤水";
        List<Word.SimilarWord> similarWorList = new ArrayList<>();
        InputAnalyzerUtil.analyzeInputSimilarWords(similarWord, similarWorList);
        Word word = new Word();
        word.setInputSimilarWords(similarWord);
        word.setSimilarWordList(similarWorList);
        System.out.println(new Gson().toJson(word));
    }


    void testAnalazeFromJson() {
        String jasonWord = "{\"name\":\"credit\",\"strangeDegree\":10,\"lastRememberTime\":1517469814369," +
                "\"meaningList\":" +
                "[{\"ciXing\":\"v\",\"isGuai\":false,\"isSheng\":true,\"meaning\":\"相信，信任\"}," +
                "{\"isGuai\":true, sdfs\"isSheng\":true,\"meaning\":\"赞颂，把...归功于\",\"ciXing\":\"v\"}," +
                "{\"isGuai\":false,\"isSheng\":false,\"meaning\":\"信任，学分，声望\",\"ciXing\":\"n\"}]}\n";
        Word word1 = new Gson().fromJson(jasonWord, Word.class);
        System.out.println(word1.getName() + "\n" + new Gson().toJson(word1.getMeaningList()) + "\n"
                + word1.getStrangeDegree() + "\n" + word1.getLastRememberTime());
    }

    void testInputWordTag() {
//        String one = "@祥光分灯光丰登 东等地";
        String one = "@祥光 分 ";
//        Matcher tagMather = Pattern.compile("%@([^@]|[\\w])+%u").matcher(one);
        Matcher tagMather = Pattern.compile("@.*?\\s").matcher(one);
        while(tagMather.find()) {
            System.out.println(tagMather.group());
        }
    }
}
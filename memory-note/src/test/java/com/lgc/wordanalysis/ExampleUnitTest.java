package com.lgc.wordanalysis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lgc.wordanalysis.base.WordDisplayAnalyzer;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.data.Word;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lgc.wordanalysis.data.Word.WordMeaning;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
//        testWordInput();
//        testAnalyzeSimilarWord();
//        testInputWordTag();
//        textInputNoTagMeaning();
//        textInputMeanings(new Word());
//        ma();
//        testInputName();
        testAnalazeFromJson();
    }

    private void stringAgTest() {
//        List<String> strings = AlgorithmUtil.StringAg.splitChineseWord("我；宣布，中华 人民,共和国， 站起来了，从此，屹立于世界民族之林。" +
//                "\n毛泽东-1949年10月1日");
//        for (String string : strings) {
//            System.out.println(string);
//        }
    }

    public void testInputName() {
        String inputName = "sdfsdf-sdfs' bn/dfsd";
        System.out.println(Pattern.compile(Word.NOT_NAME_PHRASE_REGEX).matcher(inputName).find());
    }
    public  void ma() {
        System.out.println(("ad" + "\ndddd 中的等ddd " + " ddd").replaceAll("\\b" + "ddd" + "\\W", "aaa"));
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
        WordDisplayAnalyzer.analyzeInputMeaning(input, word);
        word.setInputMeaning(input);
        System.out.println(new Gson().toJson(word));
    }

    public void testInputSimilar(Word word) {
        String input = "sdfsd word id good // 哄哄的东方盛世房东发的" +
                "hahaha// 双方双方双方爽肤水";
        List<Word.SimilarWord> similarList = new ArrayList<>();
        WordDisplayAnalyzer.analyzeInputSimilarWords(input, similarList);
        System.out.println(similarList);
        word.setSimilarWordList(similarList);
    }

    private void textInputNoTagMeaning() {
        WordMeaning wordMeaning = new WordMeaning();
        WordDisplayAnalyzer.analysisNoTagMeaning(wordMeaning, "n .   中国,rt, 日本 ");
        System.out.println(new Gson().toJson(wordMeaning));
    }

    private void testAnalyzeSimilarWord() {
        String similarWord = "sdfsd word id good v 哄哄的东方盛世房东发的\n" +
                "hahaha v&n 双方双方双方爽肤水\n" +
                "third meaning adj.第三个";
        List<Word.SimilarWord> similarWorList = new ArrayList<>();
        WordDisplayAnalyzer.analyzeInputSimilarWords(similarWord, similarWorList);
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
        List<Word> src = new ArrayList<>();
        src.add(word1);
        src.add(word1);
        src.add(word1);
        Gson gson = new Gson();
        String s = gson.toJson(src);
        List<Word> wordList = new ArrayList<>();
        try {
            Type aClass = new TypeToken<ArrayList<Word>>() {
            }.getType();
            wordList = gson.fromJson(s, aClass);
        }catch (Exception e) {
            Logcat.e(e.getMessage());
        }
        for (Word word : wordList) {
            System.out.println(word.getName());
        }
    }

    void testInputWordTag() {
//        String one = "@祥光分灯光丰登 东等地";
        String one = "@祥光 分 ";
//        Matcher tagMather = Pattern.compile("%@([^@]|[\\w])+%u").matcher(one);
        Matcher tagMather = Pattern.compile("@.*?\\s").matcher(one);
        while (tagMather.find()) {
            System.out.println(tagMather.group());
        }
    }

}
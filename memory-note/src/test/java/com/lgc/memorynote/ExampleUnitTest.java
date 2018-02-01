package com.lgc.memorynote;

import com.google.gson.Gson;
import com.lgc.memorynote.Word;
import com.lgc.memorynote.WordAnalyzer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        testAnalyzeSimilarWord();
    }

    private void testAnalyzeSimilarWord() {
        String similarWord = " credit parliament parliamentary  pre-cise accuracy virtue virtuosity distract rational ";
        List<String> similarWorList = new ArrayList<>();
        WordAnalyzer.analyzeSimilarWordsFromUser(similarWord, similarWorList);
        Word word = new Word();
        word.setInputSimilarWords(similarWord);
        word.setSimilarWordList(similarWorList);
        System.out.println(new Gson().toJson(word.getSimilarWordList()));
    }


    void testAnalazeFromJson() {
        String jasonWord = "{\"word\":\"credit\",\"strangeDegree\":10,\"lastRememberTime\":1517469814369," +
                "\"meaningList\":" +
                "[{\"ciXing\":\"v\",\"isGuai\":false,\"isSheng\":true,\"meaning\":\"相信，信任\"}," +
                "{\"isGuai\":true, sdfs\"isSheng\":true,\"meaning\":\"赞颂，把...归功于\",\"ciXing\":\"v\"}," +
                "{\"isGuai\":false,\"isSheng\":false,\"meaning\":\"信任，学分，声望\",\"ciXing\":\"n\"}]}\n";
        Word word1 = new Gson().fromJson(jasonWord, Word.class);
        System.out.println(word1.getWord() + "\n" + new Gson().toJson(word1.getMeaningList()) + "\n"
                + word1.getStrangeDegree() + "\n" + word1.getLastRememberTime());
    }

    void testInputWordMeaning() {

    }
}
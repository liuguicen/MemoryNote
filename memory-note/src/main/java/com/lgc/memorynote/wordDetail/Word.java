package com.lgc.memorynote.wordDetail;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/************************************
 * 数据格式的定义：
 * 1、词义
 * 目前的词义保存在一个String中，词义分为标签，意思两部分
 * 标签以@xxx开头，
 * {@link WordMeaning#MEANING_GUAI}   表怪的词义，
 * {@link WordMeaning#MEANING_SHENG}  表示陌生的词义
 * {@link WordMeaning#MEANING_V} 等表示词性
 * 然后词义的内容用#xxxx#表示，即#xxx# 中间的内容
 * 等表示词性 括号中的为词性
 * 2、相似词，形近词
 * 简单，多个词之间用空格隔开
 *
 * 示例:
 * @v @sheng #相信，信任#
 * @v @guai @sheng #赞颂，把...归功于#
 * @n #信任，学分，声望#
 *
 * @adj #国会的，议会的#
 * **********************************/
public class Word {

    public String word;
    public List<WordMeaning> meaningList = new ArrayList<>();
    public int strangeDegree;
    public List<String> similarWordList;
    public long lastRememberTime;

    /**
     * 用户输入的原始数据，用户用户再次编辑时使用
     */
    public String inputMeaning;
    public String inputSimilarWords;

    public static class WordMeaning {

        public static final String MEANING_GUAI = "@guai"; // 词义比较怪
        public static final String MEANING_SHENG = "@sheng"; // 词义比较生
        public static final String MEANING_N = "n";
        public static final String MEANING_V = "v";
        public static final String MEANING_ADJ = "adj";
        public static final String MEANING_ADV = "adv";
        private boolean isGuai = false;
        private boolean isSheng = false;
        private String ciXing = "null";
        private String meaning;


        public void setGuai(boolean guai) {
            isGuai = guai;
        }

        public void setSheng(boolean sheng) {
            isSheng = sheng;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public boolean setCiXing(String ciXing) {
            if (WordMeaning.MEANING_N.equals(ciXing)
                    | WordMeaning.MEANING_V.equals(ciXing)
                    | WordMeaning.MEANING_ADJ.equals(ciXing)
                    | WordMeaning.MEANING_ADV.equals(ciXing)) {
                this.ciXing =ciXing;
                return true;
            }
            return false;
        }

        public boolean isGuai() {
            return isGuai;
        }

        public boolean isSheng() {
            return isSheng;
        }

        public String getMeaning() {
            return meaning;
        }

        public String getCiXing() {
            return ciXing;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        return this == obj || TextUtils.equals(this.word, ((Word) obj).word);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setStrangeDegree(int strangeDegree) {
        this.strangeDegree = strangeDegree;
    }

    public void setLastRememberTime(long lastRememberTime) {
        this.lastRememberTime = lastRememberTime;
    }

    public void setMeaningList(List<WordMeaning> meaningList) {
        this.meaningList = meaningList;
    }

    public void setSimilarWordList(List<String> similarWordList) {
        this.similarWordList = similarWordList;
    }

    public void setInputMeaning(String inputMeaning) {
        this.inputMeaning = inputMeaning;
    }

    public void setInputSimilarWords(String inputSimilarWords) {
        this.inputSimilarWords = inputSimilarWords;
    }

    public String getWord() {
        return word;
    }

    public int getStrangeDegree() {
        return strangeDegree;
    }

    public long getLastRememberTime() {
        return lastRememberTime;
    }

    public List<WordMeaning> getMeaningList() {
        return meaningList;
    }

    public List<String> getSimilarWordList() {
        return similarWordList;
    }

    public String getInputMeaning() {
        return inputMeaning;
    }

    public String getInputSimilarWords() {
        return inputSimilarWords;
    }
}

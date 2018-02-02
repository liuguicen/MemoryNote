package com.lgc.memorynote.wordDetail;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/************************************
 * 数据格式的定义：
 * 1、词义
 * 目前的词义保存在一个String中，词义分为标签，意思两部分
 * 标签以@xxx开头，
 * {@link WordMeaning#TAG_GUAI}   表怪的词义，
 * {@link WordMeaning#TAG_SHENG}  表示陌生的词义
 * {@link WordMeaning#CIXING_V} 等表示词性
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
    public int strangeDegree = 0;
    public List<String> similarWordList;
    public long lastRememberTime = 0;

    /**
     * 用户输入的原始数据，用户用户再次编辑时使用
     */
    public String inputMeaning;
    public String inputSimilarWords;

    public static int compareDictionary(String w1, String w2) {
        if (w1 == null && w2 == null) return 0;
        else if (w1 == null) return 1;
        else if (w2 == null) return -1;
        return w1.compareTo(w2);
    }

    public static class WordMeaning {

        public static final String TAG_GUAI = "guai";
        public static final String TAG_SHENG = "sheng";
        public static final String CIXING_N = "n";
        public static final String CIXING_V = "v";
        public static final String CIXIN_ADJ = "adj";
        public static final String CIXING_ADV = "adv";
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
            if (WordMeaning.CIXING_N.equals(ciXing)
                    | WordMeaning.CIXING_V.equals(ciXing)
                    | WordMeaning.CIXIN_ADJ.equals(ciXing)
                    | WordMeaning.CIXING_ADV.equals(ciXing)) {
                this.ciXing =ciXing;
                return true;
            }
            return false;
        }

        public static int getCiXingImportance(String ciXing) {
            if (WordMeaning.CIXING_N.equals(ciXing)) return 1;
            if (WordMeaning.CIXING_V.equals(ciXing)) return 2;
            if (WordMeaning.CIXIN_ADJ.equals(ciXing)) return 3;
            if(WordMeaning.CIXING_ADV.equals(ciXing)) return 4;
            return 10000;
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

        public String  getCiXing() {
            return ciXing;
        }

        public boolean hasTag(String tag) {
            return TAG_SHENG.equals(tag)
                    || TAG_GUAI.equals(tag);
        }

        /**
         * @return is the tag is valid
         */
        public boolean putTag(String tag) {
            if (TAG_SHENG.equals(tag)) {
                setSheng(true);
                return true;
            } else if (TAG_GUAI.equals(tag)) {
                setGuai(true);
                return true;
            } else return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        return this == obj || TextUtils.equals(this.word, ((Word) obj).word);
    }

    public static int compareStrangeDegree(int s1, int s2) {
        return s2 - s1; // 大的在前面，倒序
    }

    public static int compareRememberTime(long r1, long r2) {
        long re = r2 - r1;
        if (re < 0) return -1;
        if (re > 0) return 1;
        return 0;
    }

    public static int compareSimilarNumber(List<String> w1, List<String> w2) {
        int s1 = 0, s2 = 0;
        if (w1 != null) s1 = w1.size();
        if (w2 != null) s2 = w2.size();
        return s2 - s1;
    }

    public static int compareLength(String w1, String w2) {
        int l1 =  0, l2 = 0;
        if (w1 != null) l1 = w1.length();
        if (w2 != null) l2 = w2.length();
        return l2 - l1;
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

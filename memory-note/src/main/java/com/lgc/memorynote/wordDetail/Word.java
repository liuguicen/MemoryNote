package com.lgc.memorynote.wordDetail;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/************************************
 * 数据格式的定义：
 * 1、词义
 * 目前的词义保存在一个String中，词义分为三部分 @标签 词性. 词义
 * 标签以@xxx开头，后面必须是连续的字符，在后面是词性 x. 然后是词义
 * 最后每个意思必须以一行为单位，即一行一个意思
 * 2、相似词，形近词 或短语
 * 一行一个
 *
 * 示例:
 * @sheng n. 相信，信任
 * @guai @sheng v. 赞颂，把...归功于
 * n.信任，学分，声望
 *
 * adj.国会的，议会的
 * @guai @gsdf adj. adv. uauaua
 * **********************************/
public class Word {
    public static String NOT_NAME_FORMAT_REGEX = "[^a-zA-z\\-' ]";


    public String name;
    public List<WordMeaning> meaningList = new ArrayList<>();
    public int strangeDegree = 0;
    public List<SimilarWord> similarWordList;
    public long lastRememberTime = 0;

    /**
     * 用户输入的原始数据，用户用户再次编辑时使用
     */
    public String inputMeaning;
    public String inputSimilarWords;

    /**
     * @return -1 no
     *          0 contain
     *          1 startWith
     *          2 equal
     */
    public int containMeaning(String search) {
        List<Word.WordMeaning> meaningList = getMeaningList();
        if (meaningList == null) {
            return -1;
        }

        for (int j = 0; j < meaningList.size(); j++){
            String meaning = meaningList.get(j).getMeaning();
            if (meaning != null && meaning.contains(search)) {
                if (meaning.equals(search)) {
                    return 2;
                } else if (meaning.startsWith(search)) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }


    public static class WordMeaning {

        public static final String TAG_START = "@";
        public static final String CIXING_N = "n.";
        public static final String CIXING_V = "v.";
        public static final String CIXING_ADJ = "adj.";
        public static final String CIXING_ADV = "adv.";
        public static final String TAG_SHENG = "@生";
        public static final String TAG_GUAI = "@怪";

        private String ciXing;
        private String meaning;
        private List<String> tagList = new ArrayList<>();

        /**
         * @return is the tag is valid
         */
        public boolean addTag(String tag) {
            if (!tag.startsWith(TAG_START))
                return false;
            tagList.add(tag);
            return true;
        }

        public void addValidTag(String tag) {
            tagList.add(tag);
        }

        public boolean hasTag(String tag) {
            if (tagList == null || tag == null)
                return false;
            for (String hadTag : tagList) {
                if (hadTag.equals(tag)) {
                    return true;
                }
            }
            return false;
        }

        public static int getCiXingImportance(String ciXing) {
            if (WordMeaning.CIXING_N.equals(ciXing)) return 1;
            if (WordMeaning.CIXING_V.equals(ciXing)) return 2;
            if (WordMeaning.CIXING_ADJ.equals(ciXing)) return 3;
            if(WordMeaning.CIXING_ADV.equals(ciXing)) return 4;
            return 10000;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public String getMeaning() {
            return meaning;
        }

        public boolean setCiXing(String ciXing) {
            if (WordMeaning.CIXING_N.equals(ciXing)
                    | WordMeaning.CIXING_V.equals(ciXing)
                    | WordMeaning.CIXING_ADJ.equals(ciXing)
                    | WordMeaning.CIXING_ADV.equals(ciXing)) {
                this.ciXing =ciXing;
                return true;
            }
            return false;
        }

        public String  getCiXing() {
            return ciXing;
        }

        public List<String> getTagList() {
            return tagList;
        }

        public void setTagList(List<String> tagList) {
            this.tagList = tagList;
        }

        public void setValidCiXing(String validCiXing) {
            this.ciXing = validCiXing;
        }
    }

    public static class SimilarWord {
        public String name;
        public String anotation;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAnotation(String anotation) {
            this.anotation = anotation;
        }

        public String getAnotation() {
            return anotation;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        return this == obj || TextUtils.equals(this.name, ((Word) obj).name);
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

    public static int compareSimilarNumber(List<SimilarWord> w1, List<SimilarWord> w2) {
        int s1 = 0, s2 = 0;
        if (w1 != null) s1 = w1.size();
        if (w2 != null) s2 = w2.size();
        return s2 - s1;
    }

    public static int compareDictionary(String w1, String w2) {
        if (w1 == null && w2 == null) return 0;
        else if (w1 == null) return 1;
        else if (w2 == null) return -1;
        return w1.compareTo(w2);
    }

    public static int compareLength(String w1, String w2) {
        int l1 =  0, l2 = 0;
        if (w1 != null) l1 = w1.length();
        if (w2 != null) l2 = w2.length();
        return l2 - l1;
    }


    public void setName(String word) {
        this.name = word;
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

    public void setSimilarWordList(List<SimilarWord> similarWordList) {
        this.similarWordList = similarWordList;
    }

    public void setInputMeaning(String inputMeaning) {
        this.inputMeaning = inputMeaning;
    }

    public void setInputSimilarWords(String inputSimilarWords) {
        this.inputSimilarWords = inputSimilarWords;
    }

    public String getName() {
        return name;
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

    public List<SimilarWord> getSimilarWordList() {
        return similarWordList;
    }

    public String getInputMeaning() {
        return inputMeaning;
    }

    public String getInputSimilarWords() {
        return inputSimilarWords;
    }
}

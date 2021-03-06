package com.lgc.wordanalysis.data;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lgc.wordanalysis.base.AlgorithmUtil;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.base.WordDisplayAnalyzer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lgc.wordanalysis.base.AlgorithmUtil.StringAg;

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

    public static final int DEFAULT_STRANGE_DEGREE = 0;
    public static String NOT_NAME_PHRASE_REGEX = "[^a-zA-z\\-\\\\n/' ]";
    public static String NOT_WORD_REGEX = "[^a-zA-z\\-']";

    public static final int PURE_WORD = 0;
    public static final int ROOT = 1;
    public static final int PREFIX = 2;
    public static final int SUFFIX = 3;
    public static final int PHRASE = 4;
    public static final int OTHER = 10;

    public static final String TAG_START = "@";
    public static final String TAG_GUAI = TAG_START + "怪";
    public static final String TAG_SHENG = TAG_START + "生";
    public static final String TAG_DI = TAG_START + "低";
    public static final String TAG_WEI = TAG_START + "未";
    public static final String TAG_FEI = TAG_START + "非";


    public static String TAG_ROOT = TAG_START + "词根";
    public static String TAG_PREFFIX = TAG_START + "前缀";
    public static String TAG_SUFFIX = TAG_START + "后缀";
    public static String TAG_N_SUFFIX = TAG_START + "名词后缀";
    public static String TAG_V_SUFFIX = TAG_START + "动词词后缀";
    public static String TAG_ADJ_SUFFIX = TAG_START + "形容词后缀";
    public static String TAG_ADV_SUFFIX = TAG_START + "副词后缀";


    public static final int DEGREE_DI = 7;
    public static final int DEGREE_ROOT = 5;
    public static final int DEGREE_PREFFIX = 5;
    public static final int DEGREE_SUFFIX = 5;
    public static final int DEGREE_WEI = 5;
    public static final int DEGREE_FEI = 4;


    public String name;
    public List<WordMeaning> meaningList = new ArrayList<>();
    public boolean isCheckedMeaning = false;
    public int strangeDegree = 0;
    public List<SimilarWord> similarWordList;
    public List<SimilarWord> groupList;
    public List<SimilarWord> synonymList;
    public long lastRememberTime = 0;

    public List<String> tagList;

    /**
     * 上次修改时间
     **/
    public long lastModifyTime = 0;
    /**
     * 上次上传到服务器的时间, if last upload time is smaller than last modify time, should upload it
     **/
    public long lastUploadTime = 0;
    /**
     * 上次下载时间
     **/
    public long lastDownLoadTime = 0;

    /**
     * 用户输入的原始数据==显示数据，用户用户再次编辑时使用
     * 要保证显示数据和内部数据的同步
     * 应该叫displayedxxx更好，由于要和服务器、本地的Jason同步，不好改了
     */
    public String inputMeaning;
    public String inputSimilarWords;
    public String inputRememberWay;
    public String inputWordGroup;
    public String inputSynonyms;

    /**
     * @return 返回包含程度 {@link AlgorithmUtil.StringAg#EQUAL_SIMILAR}等
     */
    public double containMeaning(String search) {
        List<Word.WordMeaning> meaningList = getMeaningList();
        if (meaningList == null) {
            return StringAg.NOT_SIMILAR;
        }

        double maxDegree = StringAg.NOT_SIMILAR;
        for (int j = 0; j < meaningList.size(); j++) {
            double degree = StringAg.stringSimilar(meaningList.get(j).getMeaning(), search);
            if (degree == StringAg.EQUAL_SIMILAR) return degree;
            maxDegree = Math.max(degree, maxDegree);
        }
        return maxDegree;
    }

    public boolean hasTag(String tag) {
        if (tagList != null && tag != null) {
            return tagList.contains(tag);
        }
        return false;
    }

    public boolean hasTags(List<String> outTagList) {
        if (this.tagList != null && outTagList != null) {
            for (String outTag : outTagList) {
                if (tagList.contains(outTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setInputSynonyms(String inputSynonym) {
        this.inputSynonyms = inputSynonym;
    }

    public void setSynonymByDisplay(String inputSynonym) {
        this.inputSynonyms = inputSynonym;
        List<Word.SimilarWord> synonymList = new ArrayList<>();
        WordDisplayAnalyzer.analyzeInputSimilarWords(inputSynonym, synonymList);
        this.synonymList = synonymList;
    }

    public void setStrangeDegreeByDisplay(String s) {
        strangeDegree = Word.DEFAULT_STRANGE_DEGREE;
        if (s == null || s.isEmpty()) return;
        try {
            strangeDegree = Integer.parseInt(s);
        } catch (Exception e) {
            Log.e("DataSync", "解析单词陌生度失败");
        }
    }

    public void setLastRememberTimeByDisplay(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分"); // 注意和获取格式一致
        try {
            lastRememberTime = sdf.parse(s).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            lastRememberTime = 0;
        }//毫秒
    }

    public String getLastRememberTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分"); // 注意和设置时格式一致
        Date date = new Date(lastRememberTime);
        return sdf.format(date);
    }


    /**
     *
     */
    public static class WordMeaning {

        public static final String CIXING_N = "n";
        public static final String CIXING_V = "v";
        public static final String CIXING_ADJ = "adj";
        public static final String CIXING_ADV = "adv";

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

        /**
         * 保证Tag是有效的
         */
        public void addValidTag(String tag) {
            tagList.add(tag);
        }

        /**
         * 弃用，使用Word的方法
         */
        @Deprecated
        public boolean hasTags(String tag) {
            if (tagList == null || tag == null)
                return false;
            for (String hadTag : tagList) {
                if (hadTag.equals(tag)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * 弃用，使用Word的方法
         */
        @Deprecated
        public boolean hasTags(List<String> tagList) {
            if (this.tagList == null || tagList == null)
                return false;
            for (String hadTag : tagList) {
                if (tagList.contains(hadTag)) {
                    return true;
                }
            }
            return false;
        }

        public static int getCiXingImportance(String ciXing) {
            if (WordMeaning.CIXING_N.equals(ciXing)) return 1;
            if (WordMeaning.CIXING_V.equals(ciXing)) return 2;
            if (WordMeaning.CIXING_ADJ.equals(ciXing)) return 3;
            if (WordMeaning.CIXING_ADV.equals(ciXing)) return 4;
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
                this.ciXing = ciXing;
                return true;
            }
            return false;
        }

        public String getCiXing() {
            return ciXing;
        }

        public List<String> getTagList() {
            return tagList;
        }

        public void setTagList(List<String> tagList) {
            this.tagList = tagList;
        }

        public void putValidCiXing(String validCiXing) {
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

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SimilarWord) {
                return TextUtils.equals(name, ((SimilarWord) obj).getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (name != null)
                return name.hashCode();
            else
                return "".hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        return this == obj || TextUtils.equals(this.name, ((Word) obj).name);
    }

    /**
     * 将该子字符串与单词所有内容进行比较，返回匹配度
     *
     * @param search
     * @return {@link WordUtil}
     */
    public double matchDegree(String search) {
        if (TextUtils.isEmpty(search)) return 0;

        // 比较名称
        double degree = StringAg.stringSimilar(name, search);
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_NAME, degree);
        }

        // 比较词义
        degree = containMeaning(search);
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_MEANING, degree);
        }

        // 比较陌生度
        degree = String.valueOf(strangeDegree).contains(search) ? 1 : StringAg.NOT_SIMILAR;
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_STRANGE, degree);
        }

        // 相似
        degree = (inputSimilarWords != null && inputSimilarWords.contains(search)) ? 1 : StringAg.NOT_SIMILAR;
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_SIMILAR, degree);
        }

        // 词组
        degree = (inputWordGroup != null && inputWordGroup.contains(search)) ? 1 : StringAg.NOT_SIMILAR;
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_GROUP, degree);
        }

        // 其它
        degree = toString().contains(search) ? 1 : StringAg.NOT_SIMILAR;
        if (degree > StringAg.NOT_SIMILAR) {
            return WordUtil.getHigherMatchDegree(WordUtil.MATCH_BASE_OTHER, degree);
        }
        return 0;
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
        int l1 = 0, l2 = 0;
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

    public void setSynonymList(List<SimilarWord> synonymList) {
        this.synonymList = synonymList;
    }

    public void addMeaning(WordMeaning oneMeaning) {
        if (meaningList == null) {
            meaningList = new ArrayList<>();
        }
        meaningList.add(oneMeaning);
    }

    public void setInputMeaning(String inputMeaning) {
        this.inputMeaning = inputMeaning;
    }

    public boolean isCheckedMeaning() {
        return isCheckedMeaning;
    }

    public void setCheckedMeaning(boolean checked) {
        isCheckedMeaning = checked;
    }


    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public void addTag(String tag) {
        if (tagList == null) {
            tagList = new ArrayList<>();
        }
        tagList.add(tag);
    }

    public void addTags(List<String> outTagList) {
        if (outTagList == null) return;
        if (this.tagList == null) {
            this.tagList = new ArrayList<>();
        }
        this.tagList.addAll(outTagList);
    }

    public void setInputSimilarWords(String inputSimilarWords) {
        this.inputSimilarWords = inputSimilarWords;
    }

    public void setInputRememberWay(String inputRememberWay) {
        this.inputRememberWay = inputRememberWay;
    }

    public String getInputRememberWay() {
        return inputRememberWay;
    }

    public String getInputWordGroup() {
        return inputWordGroup;
    }

    public void setInputWordGroup(String inputWordGroup) {
        this.inputWordGroup = inputWordGroup;
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

    public String getInputMeaning() {
        return inputMeaning;
    }

    public List<WordMeaning> getMeaningList() {
        return meaningList;
    }

    @Nullable
    public List<SimilarWord> getSimilarWordList() {
        return similarWordList;
    }

    public String getInputSimilarWords() {
        return inputSimilarWords;
    }

    public String getInputSynonym() {
        return inputSynonyms;
    }

    public List<SimilarWord> getGroupList() {
        return groupList;
    }

    public List<SimilarWord> getSynonymList() {
        return synonymList;
    }

    public void setGroupList(List<SimilarWord> groupList) {
        this.groupList = groupList;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public long getLastUploadTime() {
        return lastUploadTime;
    }

    public void setLastUploadTime(long lastUploadTime) {
        this.lastUploadTime = lastUploadTime;
        Logcat.d(name + "  更新上传时间完成= " + lastUploadTime);
    }

    public void setLastDownLoadTime(long lastDownLoadTime) {
        this.lastDownLoadTime = lastDownLoadTime;
    }

    public long getLastDownLoadTime() {
        return lastDownLoadTime;
    }

    public void setGroupByDisplay(String inputWordGroup) {
        this.inputWordGroup = inputWordGroup;
        List<Word.SimilarWord> groupList = new ArrayList<>();
        WordDisplayAnalyzer.analyzeInputSimilarWords(inputWordGroup, groupList);
        this.groupList = groupList;
    }

    public void setSimilarByDisplay(String inputSimilar) {
        this.inputSimilarWords = inputSimilar;
        List<Word.SimilarWord> similarList = new ArrayList<>();
        WordDisplayAnalyzer.analyzeInputSimilarWords(inputSimilar, similarList);
        this.similarWordList = similarList;
    }

    /**
     * @return {@link Word#PURE_WORD} 等
     */
    public int getWordType() {
        if (!WordUtil.isGeneralizedWord(name))
            return OTHER;
        if (tagList != null) {
            for (String s : tagList) {
                if (TAG_ROOT.equals(s)) {
                    return ROOT;
                } else if (TAG_PREFFIX.equals(s)) {
                    return PREFIX;
                } else if (WordUtil.SUFFIX_LIST.contains(s)) {
                    return SUFFIX;
                }
            }
        }
        if (name.contains(" ")) {
            return PHRASE;
        }
        return PURE_WORD;
    }


    @Override
    public String toString() {
        // java 8 默认使用StringBuilder拼接字符串了，简化编码
        return name +
                inputMeaning +
                inputSimilarWords +
                inputWordGroup +
                inputRememberWay +
                strangeDegree +
                lastRememberTime;
    }
}

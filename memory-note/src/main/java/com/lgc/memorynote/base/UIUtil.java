package com.lgc.memorynote.base;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/04
 *      version : 1.0
 * <pre>
 */

public class UIUtil {

    /**
     * 显示词义，顺序显示，词性 + 意思，有特殊标记的就显示对应的颜色或者打上标记
     */
    public static void showMeaningList(TextView tv, Word word, String divider) {
        if (tv == null || word == null)
            return;
        // 解析词义，特殊的词意采用特殊的颜色
        StringBuilder sb = new StringBuilder();
        // 添加Tag
        List<String> tagList = word.getTagList();
        if (tagList != null) {
            for (String tag : tagList) {
                sb.append(tag).append(" ");
            }
        }

        // 添加Meaning
        List<Word.WordMeaning> meaningList = word.getMeaningList();
        if (meaningList != null) {
            for (int i = 0; i < meaningList.size(); i++) {
                Word.WordMeaning oneMeaning = meaningList.get(i);
                sb.append(oneMeaning.getMeaning());
                if (i < meaningList.size() - 1)
                    sb.append(divider);
            }
        }

        // 使用Spannable
        String meaningString = sb.toString();
        SpannableString ss = new SpannableString(meaningString);
        Matcher matcher = Pattern.compile("@.+?\\s").matcher(meaningString);
        while (matcher.find()) {
            int startId = matcher.start();
            int endId = startId + matcher.group().length();
            ss.setSpan(new ForegroundColorSpan(Color.RED), startId, endId,
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        tv.setText(ss);
    }

    public static void showSimilarWords(TextView tv, List<Word.SimilarWord> similarWordList, String divider) {
        if (tv == null)
            return;


        tv.setText(similarList2String(similarWordList, divider));
    }

    public static String similarList2String(List<Word.SimilarWord> similarWordList, String divider) {
        if(similarWordList == null || similarWordList.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < similarWordList.size(); i++) {
            Word.SimilarWord similar = similarWordList.get(i);
            sb.append(similar.getName());
            if (similar.getAnotation() != null) {
                sb.append(" ");
                sb.append(similar.getAnotation());
            }
            if (i < similarWordList.size() - 1) {
                sb.append(divider);
            }
        }
        return sb.toString();
    }

    public static String meaningList2String(List<Word.WordMeaning> meaningList) {
        StringBuilder sb = new StringBuilder();
        if (meaningList != null) {
            for (Word.WordMeaning oneMeaning : meaningList) {
                sb.append(oneMeaning.getMeaning() + "   ");
            }
        }
        return sb.toString();
    }

    public static String joinSimilar(String inputSimilarWords, List<Word.SimilarWord> similarWords) {

        String addSimilar = similarList2String(similarWords, "\n");
        if (inputSimilarWords == null)
            inputSimilarWords = "";
        if (!inputSimilarWords.isEmpty()) {
            inputSimilarWords = inputSimilarWords + "\n";
        }
        return inputSimilarWords + addSimilar;
    }

    public static List<String> getRootAffixList(String name) {
        String[] split = name.split("-");
        ArrayList<String> rootList = new ArrayList<>();
        for (String s : split) {
            if (!s.trim().isEmpty()) {
                rootList.add(s);
            }
        }
        return rootList;
    }

    public static String joinRememberWay(String inputRememberWay, ArrayList<Pair<Integer, String>> rootAffixList) {
        StringBuilder sb = new StringBuilder(inputRememberWay);
        for (Pair<Integer, String> pair : rootAffixList) {
            String pre = "";
            switch (pair.first) {
                case Word.ROOT:
                    pre = (String) MemoryNoteApplication.appContext.getText(R.string.word_root);
                    break;
                case Word.PREFIX:
                    pre = (String) MemoryNoteApplication.appContext.getText(R.string.word_prefix);
                    break;
                case Word.SUFFIX:
                    pre = (String) MemoryNoteApplication.appContext.getText(R.string.word_suffix);
                    break;
            }

            String ra = pre + pair.second;
            if (!inputRememberWay.contains(ra)) {
                sb.append(ra + "\n");
            }
        }
        return sb.toString();
    }
}

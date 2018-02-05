package com.lgc.memorynote.base;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.lgc.memorynote.wordDetail.Word;

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
    public static void showMeaningList(TextView tv, List<Word.WordMeaning> wordMeaningList, String divider) {
        if (tv == null || wordMeaningList == null || wordMeaningList.size() == 0)
            return;
        // 解析词义，特殊的词意采用特殊的颜色
        StringBuilder sb = new StringBuilder();

        String lastCixing = Word.WordMeaning.CIXING_N;
        if (lastCixing.equals(wordMeaningList.get(0).getCiXing())) {
            sb.append(lastCixing + ". ");
        }
        for (Word.WordMeaning oneMeaning : wordMeaningList) {
            if (!lastCixing.equals(oneMeaning.getCiXing())) { // xixing has changing , add divider = "\n" or others + cixing
                lastCixing = oneMeaning.getCiXing();
                if (sb.length() > 0) {
                    sb.append("\n\n\n\n");
                } if (lastCixing != null) {
                    sb.append(oneMeaning.getCiXing() + ". ");
                }
            }

            for (String tag : oneMeaning.getTagList()) {
                sb.append(tag + " ");
            }
            sb.append(oneMeaning.getMeaning());
        }
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

    public static void showSimilarWords(TextView tv, List<String> similarWordList, String divider) {
        if (tv == null || similarWordList == null || similarWordList.size() == 0)
            return;

        StringBuilder sb = new StringBuilder();
        for (String similar : similarWordList) {
            sb.append(similar + divider);
        }
        tv.setText(sb.toString());
    }
}

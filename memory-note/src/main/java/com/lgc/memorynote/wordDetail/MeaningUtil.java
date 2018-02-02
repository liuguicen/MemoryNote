package com.lgc.memorynote.wordDetail;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/02
 *      version : 1.0
 * <pre>
 */

public class MeaningUtil {
    public static void showMeaningList(TextView ivMeaning, List<Word.WordMeaning> meaningList) {
        StringBuilder sb = new StringBuilder();
        List<Integer> greenList = new ArrayList<>();
        List<Integer> redList = new ArrayList<>();
        String lastCixing = Word.WordMeaning.CIXING_N;
        for (Word.WordMeaning oneMeaning : meaningList) {
            if (!TextUtils.isEmpty(oneMeaning.getCiXing()) && !lastCixing.equals(oneMeaning.getCiXing())) {
                sb.append("\t");
                greenList.add(sb.length());
                sb.append(oneMeaning.getCiXing());
                greenList.add(sb.length());
                sb.append(" ");
            } // 添加词性的字符
            if (oneMeaning.isGuai() || oneMeaning.isSheng()) {
                redList.add(sb.length());
                sb.append(oneMeaning.getMeaning());
                redList.add(sb.length());
            } else {
                sb.append(oneMeaning.getMeaning());
            }
        }
        SpannableString ss = new SpannableString(sb.toString());
        for (int i = 0; i < greenList.size(); i++) {
            ss.setSpan(new ForegroundColorSpan(Color.GREEN), redList.get(i),redList.get(i + 1),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        for (int i = 0; i < redList.size(); i++) {
            ss.setSpan(new ForegroundColorSpan(Color.RED), redList.get(i),redList.get(i + 1),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
}

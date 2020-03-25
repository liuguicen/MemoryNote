package com.lgc.wordanalysis.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.lgc.baselibrary.baseComponent.BaseApplication;
import com.lgc.wordanalysis.data.Word;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created 0 Administrator on 2016/5/19.
 */
public class Util {


    /**
     * @return 返回字符串的最后一个数字，没找到返回-1
     */
    public static int lastDigit(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if ('0' <= s.charAt(i) && s.charAt(i) <= '9') {
                return i;
            }
        }
        return -1;
    }

    /**
     * 点是否在View内部,注意参数值都是绝对坐标
     */
    public static boolean pointInView(float x, float y, View view) {
        if (view == null) return false;
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        Rect bound = new Rect(xy[0], xy[1], xy[0] + view.getWidth(), xy[1] + view.getHeight());
        return bound.contains((int) (x + 0.5f), (int) (y + 0.5f));
    }

    public static String convertToString(List<Word.WordMeaning> meaningList) {
        StringBuilder sb = new StringBuilder();
        for (Word.WordMeaning wordMeaning : meaningList) {
            sb.append(wordMeaning.getMeaning()).append("  ");
        }
        return sb.toString();
    }


    /**
     * �򻯴�����࣬
     * ���ǹ��������ӡ����
     *
     * @author acm_lgc
     */
    public static class P {
        public static void le(Object s) {
            Log.e(s.toString(), "------");
        }

        /**
         * @param s1 便于输出产生log的内和位置
         * @param s2
         */
        public static void le(Class s1, Object s2) {
            Log.e(s1.getSimpleName(), s2.toString());
        }

        public static void le(Object s1, Object s2) {
            Log.e(s1.toString(), s2.toString());
        }

        public void lgd(String s) {
            Log.d(s, "------");
        }

        public void lgd(String s1, String s2) {
            Log.d(s1, s2);
        }

        public static void mle(Object s1, Object s2) {
        }
    }

    /**
     * Created by Administrator on 2016/5/8.
     */
    public static class RepetitiveEventFilter {
        public static long lastTime = -1;

        private RepetitiveEventFilter(){}
        /**
         *  使用的是静态变量，同一时间段只能过滤一个时间，通常只能过滤用户操作
         * @param intervalTime
         * @return
         */
        public static boolean isRepetitive(long intervalTime) {
            long curTime = System.currentTimeMillis();
            if (curTime - lastTime < intervalTime) {
                return true;
            } else {
                lastTime = curTime;
                return false;
            }
        }

        public static void cancel() {
            lastTime = -1;
        }
    }

    /**
     * 获取两点间的位置
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    float getDis(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2, dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static Drawable getStateDrawable(Drawable src, ColorStateList colors, PorterDuff.Mode mode) {
        Drawable drawable = DrawableCompat.wrap(src);
        DrawableCompat.setTintList(drawable, colors);
        DrawableCompat.setTintMode(drawable, mode);
        return drawable;
    }

    /**
     * 不要传空进去，提升性能
     * @return -1 no
     *          0 contain
     *          1 startWith
     *          2 equal
     */
    public static int containDegree(@NonNull String a, @NonNull String b) {
        if (a == b) return 2;
        if (a.equals(b)) return 2;
        if (a.startsWith(b)) return 1;
        if (a.contains(b)) return 0;
        return -1;
    }
}

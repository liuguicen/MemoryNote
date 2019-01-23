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


    public static void showInputMethod(final View v) {
        showKeyBoard(v);
    }

    public static void showKeyBoard(final View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        }
    }

    //显示虚拟键盘
    public static void showKeyboard(final View v, long delay)
    {
        if (delay > 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    showKeyBoard(v);
                }
            }, delay);
        } else {
            showKeyBoard(v);
        }
    }

    //隐藏虚拟键盘
    public static void hideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
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

    public static void T(Context context, Object s) {
        Toast.makeText(context, s.toString(), Toast.LENGTH_LONG).show();
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

    public static void getMesureWH(View v, int[] WH) {
        int width = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        int height = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        v.measure(width, height);
        WH[0] = v.getMeasuredWidth();
        WH[1] = v.getMeasuredHeight();
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

    public static int getColor(Context context, @ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return context.getResources().getColor(id, null);
        return context.getResources().getColor(id);
    }

    public static Drawable getStateDrawable(Drawable src, ColorStateList colors, PorterDuff.Mode mode) {
        Drawable drawable = DrawableCompat.wrap(src);
        DrawableCompat.setTintList(drawable, colors);
        DrawableCompat.setTintMode(drawable, mode);
        return drawable;
    }

    /**
     * 把long 转换成 日期 再转换成String类型
     * transferLongToDate("yyyy-MM-dd HH:mm:ss",1245678944);
     */
   public static String long2Date(String dateFormat, Long millSec) {
       SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
       Date date = new Date(millSec);
       return sdf.format(date);
   }

   public static String long2DateDefult(long millSec) {
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
       Date date = new Date(millSec);
       return sdf.format(date);
   }

    // 获取Map中的Value的Set
    public static <T> Set<T> map2set(Map<?, T> map) {
        Set<T> set = new LinkedHashSet<>();
        for (Map.Entry<?, T> entry : map.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
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
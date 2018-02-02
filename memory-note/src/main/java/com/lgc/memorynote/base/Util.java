package com.lgc.memorynote.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created 0 Administrator on 2016/5/19.
 */
public class Util {

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

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
    public static class DoubleClick {
        public static long lastTime = -1;

        public static boolean isDoubleClick() {
            long curTime = System.currentTimeMillis();
            //貌似系统定义的双击正是300毫秒 ViewConfiguration.getDoubleTapTimeout()
            if (curTime - lastTime < ViewConfiguration.getDoubleTapTimeout()) {
                lastTime = curTime;
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
}

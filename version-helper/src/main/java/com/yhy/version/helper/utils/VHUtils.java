package com.yhy.version.helper.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-04-11 16:05
 * version: 1.0.0
 * desc   : View工具类
 */
public class VHUtils {

    private VHUtils() {
        throw new UnsupportedOperationException("Can not create instance for class VHUtils.");
    }

    /**
     * 移除view的父控件
     *
     * @param view view
     */
    public static void removeParent(View view) {
        if (null != view && null != view.getParent() && view.getParent() instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
        }
    }

    /**
     * dp转px
     *
     * @param context 上下文对象
     * @param dpVal   dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context
                .getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context 上下文对象
     * @param spVal   sp值
     * @return px值
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context
                .getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context 上下文对象
     * @param pxVal   px值
     * @return dp值
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context 上下文对象
     * @param pxVal   px值
     * @return sp值
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 判断一个view是否接收到事件
     *
     * @param view  要判断的view
     * @param event 当前的事件
     * @return 是否在view的范围内
     */
    public static boolean isViewEventRange(View view, MotionEvent event) {
        if (null != view) {
            int left = view.getLeft();
            int top = view.getTop();
            int right = left + view.getWidth();
            int bottom = top + view.getHeight();
            return event.getX() >= left && event.getX() <= right && event.getY() >= top && event.getY() <= bottom;
        }
        return false;
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文对象
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = dp2px(context, result);
        }
        return result;
    }
}

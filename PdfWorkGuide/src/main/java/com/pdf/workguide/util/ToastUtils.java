package com.pdf.workguide.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pdf.workguide.R;


/**
 * Create by fanxw
 * 这个自定义的Toast用于频繁弹出Toast时取消之前的toast，只显示最后一个Toast
 */
@SuppressLint("WrongConstant")
public class ToastUtils {
    public static final int LENGTH_SHORT = 1000;
    public static final int LENGTH_MID = 1500;
    public static final int LENGTH_LONG = 2000;
    public static final int LENGTH_MAX_LONG = 3000;

    private static Toast toast;

    private static void showToast(Context ctx, CharSequence msg, int duration) {
        if (null != toast) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, msg, duration);
        //在中间显示
        //toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, ScreenUtils.dip2px(ctx, 56));
        toast.show();
    }

    /**
     * 弹出Toast
     *
     * @param ctx      弹出Toast的上下文
     * @param msg      弹出Toast的内容
     * @param duration 弹出Toast的持续时间
     */
    public static void show(Context ctx, CharSequence msg, int duration)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        if (0 > duration) {
            duration = LENGTH_SHORT;
        }
        showToast(ctx, msg, duration);
    }

    /**
     * 弹出Toast
     *
     * @param ctx      弹出Toast的上下文
     * @param resId    弹出Toast的内容的资源ID
     * @param duration 弹出Toast的持续时间
     */
    public static void show(Context ctx, int resId, int duration)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        if (0 > duration) {
            duration = LENGTH_SHORT;
        }
        showToast(ctx, ctx.getResources().getString(resId), duration);
    }

    /**
     * 弹出Toast
     *
     * @param ctx   弹出Toast的上下文
     * @param resId 弹出Toast的内容的资源ID
     */
    public static void showShort(Context ctx, int resId)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        showToast(ctx, ctx.getResources().getString(resId), LENGTH_SHORT);
    }

    /**
     * 弹出Toast
     *
     * @param ctx 弹出Toast的上下文
     * @param msg 弹出Toast的内容
     */
    public static void showShort(Context ctx, CharSequence msg)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        showToast(ctx, msg, LENGTH_SHORT);
    }

    //    public static void showError(Context ctx, int resId)
//        throws NullPointerException {
//        if (null == ctx) {
//            throw new NullPointerException("The ctx is null!");
//        }
//        if (null != toast) {
//            toast.cancel();
//        }
//        toast = Toast.makeText(ctx, resId, LENGTH_MAX_LONG);
//        //在中间显示
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, ScreenUtil.dip2px(ctx,56));
//        View view = View.inflate(ctx, R.layout.no_word_toast_view, null);
//        TextView tvMsg = view.findViewById(R.id.tvMsg);
//        tvMsg.setText(resId);
//        //在中间显示
//        toast.setView(view);
//        toast.show();
//    }
//
    public static void showError(Context ctx, String msg)
            throws NullPointerException {
        initToast(ctx, msg);
        View view = View.inflate(ctx, R.layout.no_word_toast_view, null);
        TextView tvMsg = view.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        //在中间显示
        toast.setView(view);
        toast.show();
    }

    public static void showNomal(Context ctx, String msg)
            throws NullPointerException {
        initToast(ctx, msg);
        View view = View.inflate(ctx, R.layout.no_word_toast_view, null);
        TextView tvMsg = view.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        tvMsg.setBackgroundResource(R.drawable.toast_nomal);
        //在中间显示
        toast.setView(view);
        toast.show();
    }

    public static void showNomal(Context ctx, int msg)
            throws NullPointerException {
        showNomal(ctx, ctx.getResources().getString(msg));
    }

    public static void showError(Context ctx, int msg)
            throws NullPointerException {
        showError(ctx, ctx.getResources().getString(msg));
    }

    private static void initToast(Context ctx, String msg) {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        if (null != toast) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, msg, LENGTH_MAX_LONG);
        //在中间显示
        //toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int) ctx.getResources().getDimension(R.dimen.title_height));
    }
}

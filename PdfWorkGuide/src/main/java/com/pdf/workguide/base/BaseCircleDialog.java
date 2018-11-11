package com.pdf.workguide.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Description:
 * Data: 2018/11/11
 *
 * @author: cqian
 */
public class BaseCircleDialog extends Dialog {
    private static int default_width = 140; // 默认宽度
    private static int default_height = 120;// 默认高度

    public BaseCircleDialog(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public BaseCircleDialog(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public BaseCircleDialog(Context context, int width, int height, View layout, int style, int type) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}

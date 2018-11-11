package com.pdf.workguide.util;

import android.content.Context;
import android.text.TextUtils;

import com.tamic.novate.Throwable;

import java.lang.ref.SoftReference;

/**
 * Description:
 * Data: 2018/7/24
 *
 * @author: cqian
 */

public class ErrorLogUtils {

    public static void showError(Throwable e, Context context) {
        SoftReference<Context> softReference = new SoftReference<Context>(context);
        if (e != null && (!TextUtils.isEmpty(e.getMessage()))) {
            int code = -1;
            try {
                code = Integer.valueOf(e.getCode());
            } catch (Exception e1) {
            }
            String message = e.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = "请求失败";
            }
            ToastUtils.showError(softReference.get(), message);

        }

    }

}

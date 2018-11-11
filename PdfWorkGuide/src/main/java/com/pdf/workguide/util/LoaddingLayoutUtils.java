package com.pdf.workguide.util;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.pdf.workguide.R;
import com.tuyenmonkey.mkloader.MKLoader;


/**
 * Description:
 * Data: 2018/7/5
 *
 * @author: cqian
 */

public class LoaddingLayoutUtils {
    private MKLoader mLoaddingView;
    private FrameLayout mLoaddingRoot;

    public LoaddingLayoutUtils(Activity activity) {
        mLoaddingView = activity.findViewById(R.id.loaddView);
        mLoaddingRoot = activity.findViewById(R.id.loaddingRoot);
    }


    public void setVisiable(int visiable) {
        mLoaddingRoot.setVisibility(visiable);
    }

    public void show() {
        setVisiable(View.VISIBLE);
    }

    public void dismiss() {
        setVisiable(View.GONE);
    }

}

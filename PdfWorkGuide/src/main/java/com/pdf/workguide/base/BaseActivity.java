package com.pdf.workguide.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


/**
 * Created by cqian on 2018/6/7.
 */

public abstract class BaseActivity extends FragmentActivity {
    protected BaseActivity mActivity;
    protected View mRootView;
    protected View mLayoutView;
    //标记APP是否从后台再次进入的flag
    static boolean isFromBackground = false;
    static long mLastTime = -1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        beforeAddContent();
        super.onCreate(savedInstanceState);

        mActivity = this;
        mRootView = getWindow().getDecorView().getRootView();
        mLayoutView = View.inflate(this, getLayoutId(), null);

        setContentView(mLayoutView);
        initView();
        initData();
    }


    protected  void beforeAddContent(){};
    protected abstract void initView();

    protected abstract void initData();

    protected abstract int getLayoutId();

    protected boolean setPadding() {
        return true;
    }

    protected void setText(int id, String text) {
        View viewById = mRootView.findViewById(id);
        if (viewById != null && viewById instanceof TextView) {
            ((TextView) viewById).setText(text);
        } else {
            throw new RuntimeException("view为空或者view不是TextView");
        }
    }

    protected void setText(int id, int text) {
        setText(id, getResources().getString(text));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0); //强制隐藏键盘
    }

}
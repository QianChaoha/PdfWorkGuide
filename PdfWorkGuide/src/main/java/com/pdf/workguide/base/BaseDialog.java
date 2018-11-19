package com.pdf.workguide.base;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdf.workguide.R;


/**
 * Created by cqian on 2017/12/28.
 * 通用对话框父类,底部默认两个按钮(确定和取消)
 */
public abstract class BaseDialog {
    protected BaseCircleDialog mDialog;
    protected RelativeLayout mRootView;
    protected Context mContext;
    protected BaseDialogInterface mDialogInterface;
    protected FrameLayout mContentWrapper;
    protected LinearLayout mTitleWrapper;

    public BaseDialog(Context context) {
        mContext = context;
        mRootView = (RelativeLayout) View.inflate(mContext, R.layout.dialog_base, null);
        mDialog = new BaseCircleDialog(mContext, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, mRootView, R.style.Dialog);
        mRootView.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInterface.ok(v);
            }
        });
        mRootView.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInterface.cancle(v);
            }
        });
        mContentWrapper = mRootView.findViewById(R.id.contentWrapper);
        mTitleWrapper = mRootView.findViewById(R.id.titleWrapper);
        View.inflate(context, getLayoutId(), mContentWrapper);
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.8); //设置宽度
        lp.height = (int) (display.getHeight() * 0.8); //设置高度
        mDialog.getWindow().setAttributes(lp);

        mDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    /**
     * 设置底部只有一个按钮
     */
    public void setBottomOneButton() {
        mRootView.findViewById(R.id.tvOk).setVisibility(View.GONE);
        mRootView.findViewById(R.id.bottomLine).setVisibility(View.GONE);
    }

    /**
     * 设置底部没有按钮
     */
    public void setBottomNoButton() {
        mRootView.findViewById(R.id.bottomWrapper).setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        ((TextView) mRootView.findViewById(R.id.tvTitle)).setText(title);
    }

    public void setTitle(int title) {
        ((TextView) mRootView.findViewById(R.id.tvTitle)).setText(title);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setDialogInterface(BaseDialogInterface dialogInterface) {
        this.mDialogInterface = dialogInterface;
    }

    public void show() {
        mDialog.show();
    }


    public abstract int getLayoutId();

    public abstract void initView();
}

package com.pdf.workguide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;

import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.base.BaseBean;
import com.pdf.workguide.http.CallBack;
import com.pdf.workguide.http.HttpUrl;
import com.pdf.workguide.http.HttpUtils;
import com.pdf.workguide.http.LogUtils;
import com.pdf.workguide.interfaces.CommenInterface;
import com.pdf.workguide.util.ErrorLogUtils;
import com.pdf.workguide.util.FtpUtils;
import com.pdf.workguide.util.GetPermissionUtil;
import com.pdf.workguide.util.IpUtils;
import com.pdf.workguide.util.LoaddingLayoutUtils;
import com.pdf.workguide.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Description:
 * Data: 2018/7/29
 *
 * @author: cqian
 */
public class LoginActivity extends BaseActivity {

    public String mIp;
    public String mUserName;
    TextInputEditText mEtUser, mEtPassword;

    static {
        //使用static代码段可以防止内存泄漏
        //全局设置默认的 Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });
    }

    @Override
    protected void initView() {
        mEtUser = findViewById(R.id.etUser);
        mEtPassword = findViewById(R.id.etPassword);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mIp = IpUtils.getHostIP();
        GetPermissionUtil.getPermission(mActivity, 1, new CommenInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {
                ToastUtils.showError(mActivity, "获取权限失败");
            }
        });

    }


    @Override
    protected void initData() {

    }


    public void login(View view) {
        mUserName = mEtUser.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName)) {
            ToastUtils.showError(mActivity, "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showError(mActivity, "密码不能为空");
            return;
        }
        //mIp = "192.168.1.222";
        mLoaddingLayoutUtils.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoginName", mUserName);
            jsonObject.put("PassWord", password);
            jsonObject.put("PositionIp", mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.doPost(HttpUrl.LOGIN, jsonObject, new CallBack<BaseBean>() {
            @Override
            public void onSuccess(BaseBean data) {
                mLoaddingLayoutUtils.dismiss();
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("userName", mUserName);
                intent.putExtra("ip", mIp);
                startActivity(intent);

            }

            @Override
            public void onFailed(Throwable e) {
                ErrorLogUtils.showError(e, mActivity);
                mLoaddingLayoutUtils.dismiss();
            }
        });
//
//        startActivity(new Intent(mActivity, MainActivity.class));


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}

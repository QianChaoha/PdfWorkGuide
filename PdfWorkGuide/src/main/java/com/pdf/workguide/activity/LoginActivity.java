package com.pdf.workguide.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;

import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.http.CallBack;
import com.pdf.workguide.http.HttpUrl;
import com.pdf.workguide.http.HttpUtils;
import com.pdf.workguide.http.LogUtils;
import com.pdf.workguide.interfaces.CommenInterface;
import com.pdf.workguide.util.FtpUtils;
import com.pdf.workguide.util.GetPermissionUtil;
import com.pdf.workguide.util.IpUtils;
import com.pdf.workguide.util.ToastUtils;
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

    public static String mIp;
    public static String mUserName;
    TextInputEditText mEtUser, mEtPassword;

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
         mUserName = mEtUser.getText().toString();
        String password = mEtPassword.getText().toString();
//        if (TextUtils.isEmpty(mUserName)) {
//            ToastUtils.showError(mActivity, "用户名不能为空");
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.showError(mActivity, "密码不能为空");
//            return;
//        }
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("LoginName", user);
//            jsonObject.put("PassWord", password);
//            jsonObject.put("PositionIp", mIp);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        HttpUtils.doPost(HttpUrl.LOGIN, jsonObject, new CallBack() {
//            @Override
//            public void onSuccess(Object data) {
//
//            }
//
//            @Override
//            public void onFailed(Throwable e) {
//
//            }
//        });
//
//        startActivity(new Intent(mActivity, MainActivity.class));


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}

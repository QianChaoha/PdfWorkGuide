package com.pdf.workguide.activity;

import android.content.Intent;
import android.view.View;

import com.pdf.workguide.MainActivity;
import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseActivity;


/**
 * Description:
 * Data: 2018/7/29
 *
 * @author: cqian
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {

    }


    @Override
    protected void initData() {

    }

    public void login(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}

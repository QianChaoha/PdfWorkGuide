package com.pdf.workguide;

import android.app.Application;

import com.pdf.workguide.http.HttpUtils;

/**
 * Description:
 * Data: 2018/11/9
 *
 * @author: cqian
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpUtils.init(getApplicationContext());
    }
}

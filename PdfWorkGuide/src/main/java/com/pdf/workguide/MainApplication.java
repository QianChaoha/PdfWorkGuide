package com.pdf.workguide;

import android.app.Application;

import com.pdf.workguide.http.HttpUtils;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;

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
        QbSdk.initX5Environment(this,null);
        HttpUtils.init(getApplicationContext());
        Bugly.init(getApplicationContext(), "a57ce0cc06", BuildConfig.DEBUG);//bugly

    }
}

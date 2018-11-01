package com.pdf.workguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tamic.novate.Novate;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click(View view) {
        String downUrl = "http://wap.dl.pinyin.sogou.com/wapdl/hole/201512/03/SogouInput_android_v7.11_sweb.apk";
        Novate novate = new Novate.Builder(this)
                //.addParameters(parameters)
                .connectTimeout(30)
                .writeTimeout(20)
//                .baseUrl(HttpUrl.SERVER_URL)
//                .addHeader(headers)
                //.addCache(true)
                .addLog(true)
                .build();
        Map map=new HashMap();
//        novate.rxGet(downUrl, map, new RxFileCallBack(FileUtil.getBasePath(this), "test.apk") {
//
//            @Override
//            public void onStart(Object tag) {
//                super.onStart(tag);
//            }
//
//            @Override
//            public void onNext(Object tag, File file) {
//            }
//
//            @Override
//            public void onProgress(Object tag, float progress, long downloaded, long total) {
//            }
//
//            @Override
//            public void onError(Object tag, Throwable e) {
//
//            }
//
//            @Override
//            public void onCancel(Object tag, Throwable e) {
//
//            }
//
//            @Override
//            public void onCompleted(Object tag) {
//                super.onCompleted(tag);
//
//            }
//        });
    }
}

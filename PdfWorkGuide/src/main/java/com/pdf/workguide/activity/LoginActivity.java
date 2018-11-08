package com.pdf.workguide.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;

import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.util.FtpUtils;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxFileCallBack;
import com.tamic.novate.util.FileUtil;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;


/**
 * Description:
 * Data: 2018/7/29
 *
 * @author: cqian
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //初始化和FTP服务器交互的类
        InitFTPServerSetting();

    }


    @Override
    protected void initData() {

    }

    public void InitFTPServerSetting() {
        //        String downUrl = "ftp://FtpUser:lin_123456@101.200.50.2/192.168.1.222/zuoye.pdf";

        FtpUtils ftpUtils = FtpUtils.getInstance();
        boolean flag = ftpUtils.initFTPSetting("101.200.50.2", 21, "FtpUser", "lin_123456");

    }

    public void login(View view) {
        //startActivity(new Intent(this, MainActivity.class));
//        String downUrl = "ftp://FtpUser:lin_123456@101.200.50.2/192.168.1.222/zuoye.pdf";

        new Thread() {
            @Override
            public void run() {
//                String downUrl = "101.200.50.2";
//                FtpUtils.downFile(downUrl, 21, "FtpUser", "lin_123456", "AllFile", "11.jpg", Environment.getExternalStorageDirectory().getPath());
                FtpUtils.getInstance().downLoadFile( "/AllFile",Environment.getExternalStorageDirectory().getPath()+"/11.jpg","11.jpg");
            }
        }.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}

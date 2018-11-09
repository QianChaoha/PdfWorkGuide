package com.pdf.workguide.activity;

import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pdf.workguide.R;
import com.pdf.workguide.adapter.HomeAdapter;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.http.CallBack;
import com.pdf.workguide.http.HttpUrl;
import com.pdf.workguide.http.HttpUtils;
import com.pdf.workguide.util.FtpUtils;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pdf.workguide.activity.LoginActivity.mIp;
import static com.pdf.workguide.activity.LoginActivity.mUserName;

public class MainActivity extends BaseActivity {
    RecyclerView mRecycleViewLeft, mRecycleViewRight, mRecycleViewBottom;
    final String[] mLeftArr = new String[]{"工位号", "员工编号", "产品品名", "产品品号", "产品工序", "计划产量",
            "计划时间", "实际产量", "不良数", "不良率", "达成率"};
    final String[] mBottomArr = new String[]{"正常", "离岗", "品质", "故障", "替岗"};
    List<String> mLeftData = new ArrayList<String>();
    List<String> mRightData = new ArrayList<String>();
    List<String> mBottomData = new ArrayList<String>();

    @Override
    protected void initView() {
        mRecycleViewLeft = findViewById(R.id.recycleViewLeft);
        mRecycleViewLeft.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecycleViewRight = findViewById(R.id.recycleViewRight);
        mRecycleViewRight.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecycleViewBottom = findViewById(R.id.recycleViewBottom);
        mRecycleViewBottom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //初始化和FTP服务器交互的类
        InitFTPServerSetting();

    }

    @Override
    protected void initData() {
        for (String data : mLeftArr) {
            mLeftData.add(data);
            mRightData.add("内容");
        }
        for (String data : mBottomArr) {
            mBottomData.add(data);
        }
        mRecycleViewLeft.post(new Runnable() {
            @Override
            public void run() {
                int itemHeight = mRecycleViewLeft.getHeight() / mLeftArr.length;
                mRecycleViewLeft.setAdapter(new HomeAdapter(mActivity, mLeftData, itemHeight));
                mRecycleViewRight.setAdapter(new HomeAdapter(mActivity, mRightData, itemHeight));

                int bottomItemHeight = mRecycleViewLeft.getHeight() / mBottomArr.length;
                mRecycleViewBottom.setAdapter(new HomeAdapter(mActivity, mBottomData, bottomItemHeight));
            }
        });
        //获取工位信息
        getPositionData();
        getPdf();
    }

    public void InitFTPServerSetting() {
        //        String downUrl = "ftp://FtpUser:lin_123456@101.200.50.2/192.168.1.222/zuoye.pdf";

        FtpUtils ftpUtils = FtpUtils.getInstance();
        boolean flag = ftpUtils.initFTPSetting("101.200.50.2", 21, "FtpUser", "lin_123456");

    }

    public void click(View view) {

    }

    public void getPdf() {
        new Thread() {
            @Override
            public void run() {
//                String downUrl = "101.200.50.2";
//                FtpUtils.downFile(downUrl, 21, "FtpUser", "lin_123456", "AllFile", "11.jpg", Environment.getExternalStorageDirectory().getPath());
                FtpUtils.getInstance().downLoadFile("/AllFile", Environment.getExternalStorageDirectory().getPath() + "/11.jpg", "11.jpg");


            }
        }.start();
    }

    public void getPositionData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoginName", mUserName);
            jsonObject.put("PositionIp", mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.doPost(HttpUrl.GET_POSITION_IP, jsonObject, new CallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}

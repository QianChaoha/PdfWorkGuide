package com.pdf.workguide.activity;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdf.workguide.R;
import com.pdf.workguide.adapter.HomeAdapter;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.base.BaseDialogInterface;
import com.pdf.workguide.bean.PositionBadInfoBean;
import com.pdf.workguide.bean.PositionInfoBean;
import com.pdf.workguide.bean.TermialFileListBean;
import com.pdf.workguide.http.CallBack;
import com.pdf.workguide.http.HttpUrl;
import com.pdf.workguide.http.HttpUtils;
import com.pdf.workguide.util.ErrorLogUtils;
import com.pdf.workguide.util.FileUtils;
import com.pdf.workguide.util.FtpUtils;
import com.pdf.workguide.view.dialog.ListDialog;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.pdf.workguide.http.HttpUrl.FILE_SERVER_URL;

public class MainActivity extends BaseActivity {
    RecyclerView mRecycleViewLeft, mRecycleViewRight;
    LinearLayout mLlBottom;
    final String[] mLeftArr = new String[]{"工位号", "员工编号", "产品品名", "产品品号", "产品工序", "计划产量",
            "计划时间", "实际产量", "不良数", "不良率", "达成率"};
    List<String> mLeftData = new ArrayList<String>();
    List<String> mRightData = new ArrayList<String>();
    List<String> mBottomData = new ArrayList<String>();
    private String mUserName;
    private String mIp;
    private HomeAdapter mHomeRightAdapter;
    ListDialog mListDialog;
    private PositionInfoBean.DataBean.TerminalInfoBean mData;

    @Override
    protected void initView() {
        mRecycleViewLeft = findViewById(R.id.recycleViewLeft);
        mRecycleViewLeft.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecycleViewRight = findViewById(R.id.recycleViewRight);
        mRecycleViewRight.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mLlBottom = findViewById(R.id.llBottom);
        //初始化和FTP服务器交互的类
        InitFTPServerSetting();
        mListDialog = new ListDialog(mActivity) {
            @Override
            public void itemClick(View view, int position, PositionBadInfoBean.DataBean dataBean) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("PositionIp", mIp);
                    if (mData != null) {
                        jsonObject.put("ProductDetailId", mData.ProductDetailId);
                    }
                    jsonObject.put("ProcessErrorId", dataBean.Id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtils.doPost(HttpUrl.GET_POSITION_BAD, jsonObject, new CallBack() {
                    @Override
                    public void onSuccess(Object data) {

                    }

                    @Override
                    public void onFailed(Throwable e) {

                    }
                });
            }
        };
        mListDialog.setDialogInterface(new BaseDialogInterface() {
            @Override
            public void ok(View view) {
                mListDialog.dismiss();
            }

            @Override
            public void cancle(View view) {
                mListDialog.dismiss();
            }
        });
    }

    @Override
    protected void initData() {
        for (String data : mLeftArr) {
            mLeftData.add(data);
        }
        mRecycleViewLeft.post(new Runnable() {
            @Override
            public void run() {
                int itemHeight = mRecycleViewLeft.getHeight() / mLeftArr.length;
                mRecycleViewLeft.setAdapter(new HomeAdapter(mActivity, mLeftData, itemHeight));
                mHomeRightAdapter = new HomeAdapter(mActivity, mRightData, itemHeight);
                mRecycleViewRight.setAdapter(mHomeRightAdapter);


                //获取工位信息
                getPositionData();
                getPdf();
            }
        });

    }

    public void InitFTPServerSetting() {
        //        String downUrl = "ftp://FtpUser:lin_123456@101.200.50.2/192.168.1.222/zuoye.pdf";

        FtpUtils ftpUtils = FtpUtils.getInstance();
        boolean flag = ftpUtils.initFTPSetting(FILE_SERVER_URL, 26, "FtpUser", "lin_123456");

    }

    public void click(View view) {

    }

    public void getPdf() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PositionIp", mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.GET_TERMINAL_FILE_LIST, jsonObject, new CallBack<TermialFileListBean>() {
            @Override
            public void onSuccess(final TermialFileListBean data) {
                if (data != null && data.data != null && data.data.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            for (int i = 0; i < data.data.size(); i++) {
                                TermialFileListBean.DataBean dataBean = data.data.get(i);
                                if (dataBean != null && !TextUtils.isEmpty(dataBean.FileIssuedPositionUrl)) {
                                    String fileName = dataBean.FileIssuedPositionUrl;
                                    String remotePath = "";
                                    if (fileName.contains("\\")) {
                                        int index = fileName.lastIndexOf("\\");
                                        if (index > 0) {
                                            remotePath = fileName.substring(0, index);
                                            fileName = fileName.substring(index + 1, fileName.length());
                                        }
                                    }
                                    operateFile(dataBean, remotePath, fileName);
                                }
                            }
                        }
                    }.start();

                }
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    private void operateFile(TermialFileListBean.DataBean dataBean, final String remotePath, final String fileName) {
        if (dataBean.ClientIsDownload) {
            if (dataBean.IsDeleted) {
                //从缓存中删除
                if (FileUtils.fileExist(mActivity, fileName)) {
                    if (FileUtils.deletefile(mActivity, fileName)) {
                        terminalPositionFileEdit(dataBean.PositionFileId);
                    }
                }
            } else {
                //重新下载到缓存
                File dir = FileUtils.getCacheDir(mActivity);// 获取缓存所在的文件夹
                File file = new File(dir, fileName);
                System.out.println("remotePath  " + remotePath);
                System.out.println("fileName  " + fileName);
                // remotePath   "/AllFile"
                ///storage/emulated/0/Android/data/com.pdf.workguide/cache/Dingan/JMeter相关的问题（整理）.docx
                FtpUtils.getInstance().downLoadFile(remotePath, file.getAbsolutePath().toString(), fileName);


            }
        }

    }

    private void terminalPositionFileEdit(int positionFileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PositionFileId", positionFileId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.TERMINAL_FILE_EDIT, jsonObject, new CallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    public void getPositionData() {
        if (getIntent() == null) return;
        mUserName = getIntent().getStringExtra("userName");
        mIp = getIntent().getStringExtra("ip");
        mLoaddingLayoutUtils.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoginName", mUserName);
            jsonObject.put("PositionIp", mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.doPost(HttpUrl.GET_POSITION_IP, jsonObject, new CallBack<PositionInfoBean>() {
            @Override
            public void onSuccess(PositionInfoBean data) {
                mLoaddingLayoutUtils.dismiss();
                if (data != null && data.data != null) {
                    if (data.data.terminalInfo != null) {
                        setRightData(data.data.terminalInfo);
                    }
                    if (data.data.errorCategoryList != null && data.data.errorCategoryList.size() > 0) {
                        setBottomData(data.data.errorCategoryList);
                    }
                }
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingLayoutUtils.dismiss();
                ErrorLogUtils.showError(e, mActivity);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void setRightData(PositionInfoBean.DataBean.TerminalInfoBean data) {
        mData = data;
        mRightData.clear();
        //工位号
        mRightData.add(data.PositionName);
        //员工编号
        mRightData.add(mUserName);
        //产品名称
        mRightData.add(data.ProductName);
        //产品编号
        mRightData.add(data.ProductCode);
        //产品工序
        mRightData.add(data.ProcessName);
        //计划产量
        mRightData.add(data.ProductPlanNumber + "");
        //计划时间
        mRightData.add(data.ProductPlanDate);

        //实际产量
        mRightData.add("0");
        //不良数
        mRightData.add("");
        getBadNum(data.ProductDetailId);
        //不良率
        mRightData.add("0");
        //达成率
        mRightData.add("0");
        mHomeRightAdapter.notifyDataSetChanged();
    }

    /**
     * 实时获取不良数
     *
     * @return
     */
    public void getBadNum(String ProductDetailId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PositionIp", mIp);
            //工序唯一标识
            jsonObject.put("ProductDetailId", ProductDetailId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.GET_POSITION_BAD_NUMBER, jsonObject, new CallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    /**
     * 设置底部数据
     *
     * @param bottomData
     */
    public void setBottomData(final List<PositionInfoBean.DataBean.ErrorCategoryListBean> bottomData) {
        findViewById(R.id.bottomLine).setVisibility(View.VISIBLE);
        int totalWidth = mLlBottom.getWidth();
        int itemWidth = totalWidth / bottomData.size();
        for (int i = 0; i < bottomData.size(); i++) {
            TextView textView = new TextView(mActivity);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            if (bottomData.get(i) != null) {
                textView.setText(bottomData.get(i).CodeDescribe);
            }
            mLlBottom.addView(textView, new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            final int j = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickBottomBt(bottomData.get(j));
                }
            });
        }
    }

    /**
     * 底部按钮点击事件
     *
     * @param errorCategoryListBean
     */
    private void clickBottomBt(PositionInfoBean.DataBean.ErrorCategoryListBean errorCategoryListBean) {
        if (errorCategoryListBean != null) {
            mLoaddingLayoutUtils.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("processErrorCode", errorCategoryListBean.Code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtils.doPost(HttpUrl.GET_POSITION_BAD_INFO, jsonObject, new CallBack<PositionBadInfoBean>() {
                @Override
                public void onSuccess(PositionBadInfoBean data) {
                    mLoaddingLayoutUtils.dismiss();
                    mListDialog.setContent(data);
                    mListDialog.show();
                }

                @Override
                public void onFailed(Throwable e) {
                    mLoaddingLayoutUtils.dismiss();
                    ErrorLogUtils.showError(e, mActivity);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FtpUtils.getInstance().close();
    }
}

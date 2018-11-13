package com.pdf.workguide.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.pdf.workguide.R;
import com.pdf.workguide.adapter.HomeAdapter;
import com.pdf.workguide.base.BaseActivity;
import com.pdf.workguide.base.BaseBean;
import com.pdf.workguide.base.BaseDialogInterface;
import com.pdf.workguide.bean.BadPositionBean;
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
    private static final int BAD_NUM = 1;//不良数
    private static final int GET_FILE_LIST = 2;//文件列表
    private static final int POST_DELAY = 500;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BAD_NUM:
                    getBadNum();
                    break;
                case GET_FILE_LIST:
                    getPdf();
                    break;
            }
        }
    };
    private int mProductDetailId;
    private String mDisplayFile;//需要展示出来的文件名
    PDFView mPDFView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mPDFView = findViewById(R.id.pdfView);

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
                    jsonObject.put("LoginName", mUserName);
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
        FtpUtils ftpUtils = FtpUtils.getInstance();
        ftpUtils.initFTPSetting(FILE_SERVER_URL, 26, "FtpUser", "lin_123456");

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
                new Thread() {
                    @Override
                    public void run() {
                        if (data != null && data.data != null && data.data.size() > 0) {
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
                        mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
                    }
                }.start();

            }

            @Override
            public void onFailed(Throwable e) {
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
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
                if (FtpUtils.getInstance().downLoadFile(remotePath, file.getAbsolutePath().toString(), fileName) && file.exists()) {
                    if (fileName != null && fileName.equals(mDisplayFile)) {
                        showPdf();
                    }
                    terminalPositionFileEdit(dataBean.PositionFileId);
                }
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
        HttpUtils.doPost(HttpUrl.TERMINAL_FILE_EDIT, jsonObject, new CallBack<BaseBean>() {
            @Override
            public void onSuccess(BaseBean data) {

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

        HttpUtils.doPost(HttpUrl.GET_POSITION_INFO, jsonObject, new CallBack<PositionInfoBean>() {
            @Override
            public void onSuccess(PositionInfoBean data) {
                mLoaddingLayoutUtils.dismiss();
                if (data != null && data.data != null) {
                    if (data.data.terminalInfo != null) {
                        setRightData(data.data.terminalInfo);

                        mDisplayFile = data.data.terminalInfo.FileIssuedPositionUrl;
                        if (mDisplayFile != null && mDisplayFile.contains("\\")) {
                            int index = mDisplayFile.lastIndexOf("\\");
                            if (index > 0) {
                                mDisplayFile = mDisplayFile.substring(index + 1, mDisplayFile.length());
                            }
                        }

                        showPdf();
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

    private void showPdf() {
        if (TextUtils.isEmpty(mDisplayFile)) return;
        if (FileUtils.fileExist(mActivity, mDisplayFile)) {
            File dir = FileUtils.getCacheDir(mActivity);// 获取缓存所在的文件夹
            File file = new File(dir, mDisplayFile);
            mPDFView.fromFile(file).load();
        }
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
        mProductDetailId = data.ProductDetailId;
        getBadNum();
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
    public void getBadNum() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PositionIp", mIp);
            //工序唯一标识
            jsonObject.put("ProductDetailId", mProductDetailId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.GET_POSITION_BAD_NUMBER, jsonObject, new CallBack<BadPositionBean>() {
            @Override
            public void onSuccess(BadPositionBean data) {
                if (data != null) {
                    mRightData.set(8, data.data + "");
                    mHomeRightAdapter.notifyDataSetChanged();
                }
                mHandler.sendEmptyMessageDelayed(BAD_NUM, POST_DELAY);
            }

            @Override
            public void onFailed(Throwable e) {
                mHandler.sendEmptyMessageDelayed(BAD_NUM, POST_DELAY);
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
        mHandler.removeMessages(BAD_NUM);
    }
}

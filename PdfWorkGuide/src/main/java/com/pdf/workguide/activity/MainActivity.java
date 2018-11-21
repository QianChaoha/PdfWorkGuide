package com.pdf.workguide.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
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
import com.pdf.workguide.util.ToastUtils;
import com.pdf.workguide.view.dialog.ListDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxFileCallBack;
import com.tamic.novate.util.FileUtil;
import com.tencent.smtt.sdk.TbsReaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements TbsReaderView.ReaderCallback {
    RecyclerView mRecycleViewLeft, mRecycleViewRight;
    LinearLayout mLlBottom;
    final String[] mLeftArr = new String[]{"工位号", "员工编号", "产品品名", "产品品号", "产品工序", "计划产量",
            "计划时间", "实际产量", "不良数", "不良率", "达成率"};
    List<String> mLeftData = new ArrayList<String>();
    List<String> mRightData = new ArrayList<String>();
    private String mUserName;
    private String mIp;
    private HomeAdapter mHomeRightAdapter;
    ListDialog mListDialog;
    private PositionInfoBean.DataBean.TerminalInfoBean mData;
    private static final int BAD_NUM = 1;//不良数
    private static final int GET_FILE_LIST = 2;//文件列表
    private static final int CHANGE_PAGE = 3;//切换页数
    private static final int POST_DELAY = 600;
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
                case CHANGE_PAGE:
                    int nextPage = mPDFView.getCurrentPage() + 1;
                    mPDFView.jumpTo(nextPage >= mPDFView.getPageCount() ? 0 : nextPage, true);
                    mHandler.sendEmptyMessageDelayed(CHANGE_PAGE, mFilePlaybackTime * 1000);
                    break;
            }
        }
    };
    private int mProductDetailId;
    private String mDisplayFile;//需要展示出来的文件名
    PDFView mPDFView;
    private int mFilePlaybackTime = -1;//多页切换时间
    SmartRefreshLayout mSmartRefreshLayout;
    final String XLS = ".xls";
    final String PDF = ".pdf";
    TbsReaderView mTbsReaderView;
    FrameLayout mShowFileWrapper;
    private AlertDialog mAlertDialog;
    ProgressBar mProgressBar;
    boolean mIsFileShow;//文件是否已经加载
    boolean mIsFirst = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mProgressBar = findViewById(R.id.progressBar);
        mShowFileWrapper = findViewById(R.id.showFileWrapper);
        mSmartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!mIsFirst) {
                    if (mTbsReaderView != null) {
                        mTbsReaderView.onStop();
                    }
                    //FtpUtils.getInstance().close();
                    //InitFTPServerSetting();
                }
                mIsFileShow = false;
                mHandler.removeMessages(BAD_NUM);
                mHandler.removeMessages(GET_FILE_LIST);
                mHandler.removeMessages(CHANGE_PAGE);
                //获取工位信息
                getPositionData();
                mIsFirst = false;

            }
        });
        mRecycleViewLeft = findViewById(R.id.recycleViewLeft);
        mRecycleViewLeft.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecycleViewRight = findViewById(R.id.recycleViewRight);
        mRecycleViewRight.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mLlBottom = findViewById(R.id.llBottom);
        //初始化和FTP服务器交互的类
        InitFTPServerSetting();
        mListDialog = new ListDialog(mActivity) {
            @Override
            public void itemClick(View view, int position, final PositionBadInfoBean.DataBean dataBean) {
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
                HttpUtils.doPost(HttpUrl.GET_POSITION_BAD, jsonObject, new CallBack<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean data) {
                        ToastUtils.showNomal(mActivity, "添加" + dataBean.ProcessErrorName + "成功");
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

                mSmartRefreshLayout.autoRefresh();
            }
        });

    }

    public void logOut(View view) {
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //    设置Title的图标
        //builder.setIcon(R.drawable.ic_launcher);
        //    设置Title的内容
        builder.setTitle("提示");
        //    设置Content来显示一个信息
        //String data="要显示的文件名称:"+mDisplayFile+"\n";

        builder.setMessage("确定退出吗？");
        //    设置一个PositiveButton
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertDialog.dismiss();
                logOut();
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertDialog.dismiss();
            }
        });
        //    显示出该对话框
        mAlertDialog = builder.show();

    }

    public void logOut() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoginName", mUserName);
            jsonObject.put("PositionIp", mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLoaddingLayoutUtils.show();
        HttpUtils.doPost(HttpUrl.LOG_OUT, jsonObject, new CallBack<BaseBean>() {
            @Override
            public void onSuccess(BaseBean data) {
                mLoaddingLayoutUtils.dismiss();
                finish();
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingLayoutUtils.dismiss();
                ErrorLogUtils.showError(e, mActivity);
            }
        });
    }

    public void InitFTPServerSetting() {
//        FtpUtils ftpUtils = FtpUtils.getInstance();
//        ftpUtils.initFTPSetting(FILE_SERVER_URL, 26, "FtpUser", "lin_123456");

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
                                    if (fileName != null && fileName.contains("\\")) {
                                        int index = fileName.lastIndexOf("\\");
                                        if (index > 0) {
                                            fileName = fileName.substring(index + 1, fileName.length());
                                        }
                                    }
                                    String dealPath = dataBean.FileIssuedPositionUrl.replaceAll("\\\\", "/");
                                    operateFile(dataBean, HttpUrl.FILE_SERVER_URL + dealPath, fileName);
                                }
                            }
                        } else {
                            showPdf();
                        }

                    }
                }.start();

            }

            @Override
            public void onFailed(Throwable e) {
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            }
        });
    }

    private void operateFile(final TermialFileListBean.DataBean dataBean, final String remotePath, final String fileName) {
        System.out.println("dataBean.ClientIsDownload " + dataBean.ClientIsDownload + " dataBean.IsDeleted " + dataBean.IsDeleted);
        if (dataBean.ClientIsDownload) {
            if (dataBean.IsDeleted) {
                //从缓存中删除
                if (FileUtils.fileExist(mActivity, fileName)) {
                    if (FileUtils.deletefile(mActivity, fileName)) {
                        terminalPositionFileEdit(dataBean.PositionFileId);
                    }
                }
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            } else {
                //重新下载到缓存
                File dir = FileUtils.getCacheDir(mActivity);// 获取缓存所在的文件夹
                File file = new File(dir, fileName);
                System.out.println("mDisplayFile  " + mDisplayFile);
                System.out.println("fileName  " + fileName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
                // remotePath   "/AllFile"
                ///storage/emulated/0/Android/data/com.pdf.workguide/cache/Dingan/JMeter相关的问题（整理）.docx

                HttpUtils.getNovate().rxGet(remotePath, new HashMap<String, Object>(), new RxFileCallBack(dir.getPath(), fileName) {

                    @Override
                    public void onStart(Object tag) {
                        super.onStart(tag);
                    }

                    @Override
                    public void onNext(Object tag, File file) {
                    }

                    @Override
                    public void onProgress(Object tag, float progress, long downloaded, long total) {
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        hideLoaddding();
                        mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCompleted(Object tag) {
                        super.onCompleted(tag);
                        hideLoaddding();
                        if (fileName != null && fileName.equals(mDisplayFile)) {
                            showPdf();
                        }
                        terminalPositionFileEdit(dataBean.PositionFileId);
                    }
                });
            }
        } else {
            mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
        }

    }

    private void hideLoaddding() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
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
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            }

            @Override
            public void onFailed(Throwable e) {
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            }
        });
    }

    public void getPositionData() {
        if (getIntent() == null) return;
        mUserName = getIntent().getStringExtra("userName");
        mIp = getIntent().getStringExtra("ip");
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
                mSmartRefreshLayout.finishRefresh();
                if (data != null && data.data != null) {
                    if (data.data.terminalInfo != null) {
                        setRightData(data.data.terminalInfo);
                        mFilePlaybackTime = data.data.terminalInfo.FilePlaybackTime;
                        mDisplayFile = data.data.terminalInfo.FileName;
                        getPdf();
                    }
                    if (data.data.errorCategoryList != null && data.data.errorCategoryList.size() > 0) {
                        setBottomData(data.data.errorCategoryList);
                    }
                }
            }

            @Override
            public void onFailed(Throwable e) {
                ErrorLogUtils.showError(e, mActivity);
            }
        });
    }

    private void showPdf() {
        if (mIsFileShow) {
            mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mDisplayFile)) return;
                if (FileUtils.fileExist(mActivity, mDisplayFile)) {
                    File dir = FileUtils.getCacheDir(mActivity);// 获取缓存所在的文件夹
                    final File file = new File(dir, mDisplayFile);
                    if (mDisplayFile.toLowerCase().endsWith(PDF)) {
                        mShowFileWrapper.removeAllViews();
                        mPDFView = new PDFView(mActivity, null);
                        mShowFileWrapper.addView(mPDFView, new FrameLayout.LayoutParams(-1, -1));
                        mPDFView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                mIsFileShow = true;
                                if (mFilePlaybackTime > 0) {
                                    mHandler.sendEmptyMessageDelayed(CHANGE_PAGE, mFilePlaybackTime * 1000);
                                }

                            }
                        }).load();
                    } else {
                        displayFile(file);

                    }
                }
                mHandler.sendEmptyMessageDelayed(GET_FILE_LIST, POST_DELAY);
            }
        });

    }

    public void displayFile(File mFile) {
        //mFile = new File("/storage/emulated/0/新建 XLSX 工作表.xlsx");
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            mShowFileWrapper.removeAllViews();
            mTbsReaderView = new TbsReaderView(this, this);
            mShowFileWrapper.addView(mTbsReaderView, new FrameLayout.LayoutParams(-1, -1));
            mHandler.removeMessages(CHANGE_PAGE);
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败

            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", mFile.toString());

            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");

            if (this.mTbsReaderView == null)
                this.mTbsReaderView = getTbsReaderView(this);
            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
                mIsFileShow = true;
            }
        } else {
        }

    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }


        str = paramString.substring(i + 1);
        return str;
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
        String time = "2018-11-14";
        if (!TextUtils.isEmpty(data.ProductPlanDate) && data.ProductPlanDate.length() > time.length()) {
            mRightData.add(data.ProductPlanDate.substring(0, time.length()));
        } else {
            mRightData.add(data.ProductPlanDate);

        }

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
        //FtpUtils.getInstance().close();
        mHandler.removeMessages(BAD_NUM);
        mHandler.removeMessages(GET_FILE_LIST);
        mHandler.removeMessages(CHANGE_PAGE);
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
}

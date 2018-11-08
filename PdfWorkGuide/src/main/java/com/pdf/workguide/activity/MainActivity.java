package com.pdf.workguide.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pdf.workguide.R;
import com.pdf.workguide.adapter.HomeAdapter;
import com.pdf.workguide.base.BaseActivity;
import com.tamic.novate.Novate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                mRecycleViewLeft.setAdapter(new HomeAdapter(mActivity,mLeftData,itemHeight));
                mRecycleViewRight.setAdapter(new HomeAdapter(mActivity,mRightData,itemHeight));

                int bottomItemHeight = mRecycleViewLeft.getHeight() / mBottomArr.length;
                mRecycleViewBottom.setAdapter(new HomeAdapter(mActivity,mBottomData,bottomItemHeight));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void click(View view) {

    }
}

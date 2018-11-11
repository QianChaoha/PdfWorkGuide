package com.pdf.workguide.view.dialog;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.pdf.workguide.R;
import com.pdf.workguide.adapter.DialogAdpter;
import com.pdf.workguide.base.BaseDialog;
import com.pdf.workguide.base.BaseViewHolder;
import com.pdf.workguide.bean.PositionBadInfoBean;

/**
 * Description:
 * Data: 2018/11/11
 *
 * @author: cqian
 */
public abstract class ListDialog extends BaseDialog {
    private RecyclerView mRecyclerView;

    public ListDialog(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_listview;
    }

    @Override
    public void initView() {
        mRecyclerView = mRootView.findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
    }

    public void setContent(final PositionBadInfoBean data) {
        if (data != null && data.data != null && data.data.size() > 0) {
            DialogAdpter dialogAdpter = new DialogAdpter(mContext, data.data);
            mRecyclerView.setAdapter(dialogAdpter);
            dialogAdpter.setOnItemClickLitener(new BaseViewHolder.onItemCommonClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    itemClick(view, position, data.data.get(position));
                }
            });
        }
    }

    public abstract void itemClick(View view, int position, PositionBadInfoBean.DataBean dataBean);
}

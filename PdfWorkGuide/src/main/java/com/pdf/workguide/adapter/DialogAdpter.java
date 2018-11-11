package com.pdf.workguide.adapter;

import android.content.Context;

import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseRecycleAdapter;
import com.pdf.workguide.base.BaseViewHolder;
import com.pdf.workguide.bean.PositionBadInfoBean;

import java.util.List;

/**
 * Description:
 * Data: 2018/11/11
 *
 * @author: cqian
 */
public class DialogAdpter extends BaseRecycleAdapter<PositionBadInfoBean.DataBean> {
    public DialogAdpter(Context context, List<PositionBadInfoBean.DataBean> dataList) {
        super(context, dataList);
    }

    @Override
    public void bindData(BaseViewHolder holder, PositionBadInfoBean.DataBean data, int position) {
        holder.setText(R.id.textView, data.ProcessErrorName);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.dialog_item;
    }
}

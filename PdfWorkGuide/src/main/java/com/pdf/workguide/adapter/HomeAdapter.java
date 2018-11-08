package com.pdf.workguide.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdf.workguide.R;
import com.pdf.workguide.base.BaseRecycleAdapter;
import com.pdf.workguide.base.BaseViewHolder;

import java.util.List;

/**
 * Description:
 * Data: 2018/11/2
 *
 * @author: cqian
 */
public class HomeAdapter extends BaseRecycleAdapter<String> {
    private int mItemHeight;

    public HomeAdapter(Context context, List dataList, int itemHeight) {
        super(context, dataList);
        mItemHeight = itemHeight;
    }

    @Override
    public void bindData(BaseViewHolder holder, String data, int position) {
        TextView textView = holder.getView(R.id.textView);
        textView.setText(data);

        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        layoutParams.height = mItemHeight;
        textView.setLayoutParams(layoutParams);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.main_item;
    }
}

package com.leo.copytoutiao.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.leo.copytoutiao.R;
import com.taoke.base.BaseRecyclerAdapter;
import com.taoke.base.BaseViewHolder;

import java.util.List;

/**
 * Created by tk on 2021/4/13
 */
public class FolderRecyclerAdapter extends BaseRecyclerAdapter<String> {
    private String mCurKind;

    public FolderRecyclerAdapter(String curKind, List<String> data, int... layoutIds) {
        super(data, layoutIds);
        mCurKind = curKind;
    }


    @Override
    public void convert(BaseViewHolder holder, int position) {
        TextView textView = holder.getView(R.id.name);
        if (mData.get(position).equals(mCurKind)) {
            textView.setTextColor(Color.BLUE);
        } else {
            textView.setTextColor(Color.BLACK);
        }
        textView.setText(mData.get(position));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onClick(mData.get(position));
                }
            }
        });
    }

}

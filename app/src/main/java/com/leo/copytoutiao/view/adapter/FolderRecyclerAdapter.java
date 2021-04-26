package com.leo.copytoutiao.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.model.bean.FolderBean;
import com.taoke.base.BaseRecyclerAdapter;
import com.taoke.base.BaseViewHolder;

import java.util.List;

/**
 * Created by tk on 2021/4/13
 */
public class FolderRecyclerAdapter extends BaseRecyclerAdapter<FolderBean> {
    private String mCurKind;
    private OnDeleteListener listener;

    public FolderRecyclerAdapter(String curKind, List<FolderBean> data, int... layoutIds) {
        super(data, layoutIds);
        mCurKind = curKind;
    }

    public void setDeleteListener(OnDeleteListener listener){
        this.listener = listener;
    }

    public interface OnDeleteListener{
        void delete(FolderBean bean, int position);
    }


    @Override
    public void convert(BaseViewHolder holder, int position) {
        TextView textView = holder.getView(R.id.name);
        if (mData.get(position).equals(mCurKind)) {
            textView.setTextColor(Color.BLUE);
        } else {
            textView.setTextColor(Color.BLACK);
        }
        textView.setText(mData.get(position).getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onClick(mData.get(position), position);
                }
            }
        });
        if (mCurKind != null){
            ImageView delete = holder.getView(R.id.delete_folder);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                if (listener != null){
                    listener.delete(getData().get(position), position);
                }
                getData().remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getData().size());
            });
        }
    }

}

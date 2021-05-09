package com.leo.copytoutiao.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.utils.HtmlUtil;
import com.leo.copytoutiao.utils.Utils;
import com.taoke.base.BaseRecyclerAdapter;
import com.taoke.base.BaseViewHolder;

import java.util.List;

/**
 * Created by tk on 2021/4/12
 */
public class NotesRecyclerAdapter extends BaseRecyclerAdapter<NoteBean> {

    private OnItemDeleteListener dListener;

    public interface NOTE_TYPE{
        int TEXT = 1; //文字笔记
        int TEXT_PIC = 2; //图文笔记
    }

    public void setDeleteListener(OnItemDeleteListener listener){
        dListener =  listener;
    }

    public NotesRecyclerAdapter(List<NoteBean> data, int... layoutIds) {
        super(data, layoutIds);
    }

    public interface OnItemDeleteListener{
        void deleteItem(NoteBean note, int position);
    }


    @Override
    public void convert(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == NOTE_TYPE.TEXT){
            holder.setText(R.id.title, mData.get(position).getTitle());
            holder.setText(R.id.content, HtmlUtil.getTextFromHtml(mData.get(position).getContent()));
            holder.setText(R.id.time, Utils.timeStamp2Date(mData.get(position).getTime()));
        } else {
            holder.setText(R.id.title, mData.get(position).getTitle());
            holder.setText(R.id.content, HtmlUtil.getTextFromHtml(mData.get(position).getContent()));
            holder.setText(R.id.time, Utils.timeStamp2Date(mData.get(position).getTime()));
            holder.setImageFromFile(R.id.image, mData.get(position).getUrl());
        }
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onClick(getData().get(position), position);
                }
            }
        });
        holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.getView(R.id.button_container).setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.setOnClickListener(R.id.delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                if (dListener != null){
                    dListener.deleteItem(getData().get(position), position);
                }
            }
        });
        holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getView(R.id.button_container).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return TextUtils.isEmpty(mData.get(position).getUrl()) ? NOTE_TYPE.TEXT : NOTE_TYPE.TEXT_PIC;
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType){
            case NOTE_TYPE.TEXT:
                return mLayoutIds[0];
            case NOTE_TYPE.TEXT_PIC:
                return mLayoutIds[1];
        }
        return mLayoutIds[0];
    }

    public OnItemDeleteListener getdListener() {
        return dListener;
    }
}

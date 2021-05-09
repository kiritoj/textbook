package com.leo.copytoutiao.view.adapter;

import android.view.View;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.utils.HtmlUtil;
import com.leo.copytoutiao.utils.Utils;
import com.taoke.base.BaseViewHolder;

import java.util.List;

public class AlarmNotesRecyclerAdapter extends NotesRecyclerAdapter {

    public AlarmNotesRecyclerAdapter(List<NoteBean> data, int... layoutIds) {
        super(data, layoutIds);
    }

    @Override
    public void convert(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == NOTE_TYPE.TEXT){
            holder.setText(R.id.title, mData.get(position).getTitle());
            holder.setText(R.id.content, HtmlUtil.getTextFromHtml(mData.get(position).getContent()));
            holder.setText(R.id.time, "将于" + Utils.timeStamp2Date(mData.get(position).getAlarmTime()) + "提醒");
        } else {
            holder.setText(R.id.title, mData.get(position).getTitle());
            holder.setText(R.id.content, HtmlUtil.getTextFromHtml(mData.get(position).getContent()));
            holder.setText(R.id.time, "将于" + Utils.timeStamp2Date(mData.get(position).getTime()) + "提醒");
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
        holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getdListener() != null){
                    getdListener().deleteItem(getData().get(position),position);
                }
            }
        });
    }
}

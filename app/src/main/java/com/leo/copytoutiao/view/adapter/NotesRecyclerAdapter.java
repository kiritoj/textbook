package com.leo.copytoutiao.view.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.utils.Utils;
import com.taoke.base.BaseRecyclerAdapter;
import com.taoke.base.BaseViewHolder;

import java.util.List;

/**
 * Created by tk on 2021/4/12
 */
public class NotesRecyclerAdapter extends BaseRecyclerAdapter<NoteBean> {

    public interface NOTE_TYPE{
        int TEXT = 1; //文字笔记
        int TEXT_PIC = 2; //图文笔记
    }

    public NotesRecyclerAdapter(List<NoteBean> data, int... layoutIds) {
        super(data, layoutIds);
    }

    @Override
    public void convert(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == NOTE_TYPE.TEXT){
            holder.setText(R.id.title, mData.get(position).title);
            holder.setText(R.id.content, mData.get(position).content);
            holder.setText(R.id.time, Utils.timeStamp2Date(mData.get(position).time));
        } else {
            holder.setText(R.id.title, mData.get(position).title);
            holder.setText(R.id.content, mData.get(position).content);
            holder.setText(R.id.time, Utils.timeStamp2Date(mData.get(position).time));
            holder.setImageWithNet(R.id.image, mData.get(position).url);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TextUtils.isEmpty(mData.get(position).url) ? NOTE_TYPE.TEXT : NOTE_TYPE.TEXT_PIC;
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
}

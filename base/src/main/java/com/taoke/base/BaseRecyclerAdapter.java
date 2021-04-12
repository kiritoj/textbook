package com.taoke.base;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mData;
    protected int[] mLayoutIds;
    public BaseRecyclerAdapter(List<T> data, int...layoutIds){
        this.mData = data;
        this.mLayoutIds = layoutIds;
    }

    public int getLayoutId(int viewType){
        return mLayoutIds[0];
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(parent,getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                convert((BaseViewHolder) holder,position);
    }

    //子类必须覆写，UI
    public abstract void convert(BaseViewHolder holder, int position);


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<T> getData(){
        return mData;
    }

    public int[] getLayoutIds(){
        return mLayoutIds;
    }

}

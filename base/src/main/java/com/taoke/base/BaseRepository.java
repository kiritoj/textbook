package com.taoke.base;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class BaseRepository<T> {

    private  List<LoadListener<T>> mListeners = new ArrayList<>();

    public interface LoadListener<T>{
        void onSuccess(List<T> result);
        void onFailed(String errMsg);
    }

    public void setListener(LoadListener<T> listener ){
        if (!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }

    public void removeListener(LoadListener<T> listener){
        mListeners.remove(listener);
    }

    public List<LoadListener<T>> getListeners(){
        return mListeners;
    }
    

}

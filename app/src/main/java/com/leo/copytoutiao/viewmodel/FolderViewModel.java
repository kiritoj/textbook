package com.leo.copytoutiao.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.bean.UserBean;
import com.leo.copytoutiao.model.repository.FolderRepository;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.model.repository.NoteRepository;
import com.taoke.base.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class FolderViewModel extends AndroidViewModel {

    private FolderRepository mFolderRep;
    private LoginRepository mLoginRep;
    private MutableLiveData<List<FolderBean>> mFolders;
    private MutableLiveData<String> mErrMsg;

    public FolderViewModel(@NonNull Application application) {
        super(application);
        mFolderRep = FolderRepository.getInstance(application.getApplicationContext());
        mLoginRep = LoginRepository.getInstance();
        mFolders = new MutableLiveData<>();
        mErrMsg = new MutableLiveData<>();
        initListener();
    }

    public void initListener(){
        mFolderRep.setListener(new BaseRepository.LoadListener<FolderBean>() {
            @Override
            public void onSuccess(List<FolderBean> result) {
                if (mFolders.getValue() == null || !mFolders.getValue().equals(result)){
                    mFolders.setValue(result);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                mErrMsg.setValue(errMsg);
            }
        });
    }

    public void addFolder(String kind){
        FolderBean bean = new FolderBean(mLoginRep.getCurrentUser().getUserId(),kind);
        if (mFolders.getValue() == null || !mFolders.getValue().contains(bean)){
            mFolderRep.insertFolder(bean);
        }
        if (mFolders.getValue() == null){
            mFolders.setValue(new ArrayList<>());
        }
        mFolders.getValue().add(bean);
    }

    public void queryFolder(int userId){
        mFolderRep.queryFolder(userId);
    }

    public void deleteFolder(FolderBean bean){
        mFolderRep.deleteFolder(bean.getUserId(),bean.getName());
    }


    public FolderRepository getFolderRep() {
        return mFolderRep;
    }

    public MutableLiveData<List<FolderBean>> getFolders() {
        return mFolders;
    }

    public MutableLiveData<String> getErrMsg() {
        return mErrMsg;
    }
}

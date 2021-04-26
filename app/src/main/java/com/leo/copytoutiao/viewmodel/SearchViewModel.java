package com.leo.copytoutiao.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.model.repository.NoteRepository;
import com.taoke.base.BaseRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<List<NoteBean>> mNotes;
    private MutableLiveData<String> mErrMsg;
    private NoteRepository mNoteRep;
    private LoginRepository mLoginRep;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mNotes = new MutableLiveData<>();
        mNoteRep = NoteRepository.getInstance(application.getApplicationContext());
        mLoginRep = LoginRepository.getInstance();
        mErrMsg = new MutableLiveData<>();
    }

    public void search(String key){
        mNoteRep.searchNote(key, mLoginRep.getCurrentUser(), new BaseRepository.LoadListener<NoteBean>() {
            @Override
            public void onSuccess(List<NoteBean> result) {
                if (mNotes.getValue() == null || !mNotes.getValue().equals(result)){
                    mNotes.setValue(result);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                mErrMsg.setValue("搜索好像出现一点问题了~");
            }
        });
    }

    public MutableLiveData<List<NoteBean>> getNotes() {
        return mNotes;
    }

    public MutableLiveData<String> getErrMsg() {
        return mErrMsg;
    }
}

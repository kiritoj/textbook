package com.leo.copytoutiao.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.model.repository.NoteRepository;
import com.taoke.base.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRep;
    private LoginRepository mLoginRep;
    private MutableLiveData<List<NoteBean>> mNoteList;
    private MutableLiveData<String> mErrMsg;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRep = NoteRepository.getInstance(application.getApplicationContext());
        mLoginRep = LoginRepository.getInstance();
        mNoteList = new MutableLiveData<>();
        mErrMsg = new MutableLiveData<>();
        initListener();

    }

    public void initListener(){
        mNoteRep.setListener(new BaseRepository.LoadListener<NoteBean>() {
            @Override
            public void onSuccess(List<NoteBean> result) {
                if (mNoteList.getValue() == null || !mNoteList.getValue().equals(result)) {
                    mNoteList.setValue(result);
                }

            }

            @Override
            public void onFailed(String errMsg) {
                mErrMsg.setValue(errMsg);
            }
        });
    }

    public void addNote(NoteBean note){
        mNoteRep.insertNote(note);
    }

    public void updateNode(NoteBean note){
        mNoteRep.updateNote(note);
    }

    public void queryNotes(){
        mNoteRep.queryNotes(mLoginRep.getCurrentUser());
    }

    //只更新list的值，不更新liveData的值
    public void addFirstNote(NoteBean note){
        if (mNoteList.getValue() == null){
            mNoteList.setValue(new ArrayList<>());
        }
        mNoteList.getValue().add(0,note);
    }

    public void updateLocalNote(NoteBean note, int index){
        mNoteList.getValue().remove(index);
        mNoteList.getValue().add(index, note);
    }

    public MutableLiveData<List<NoteBean>> getNoteList() {
        return mNoteList;
    }
}

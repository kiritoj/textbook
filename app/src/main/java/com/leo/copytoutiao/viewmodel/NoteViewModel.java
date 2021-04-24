package com.leo.copytoutiao.viewmodel;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.repository.FolderRepository;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.model.repository.NoteRepository;
import com.taoke.base.BaseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRep;
    private LoginRepository mLoginRep;
    private MutableLiveData<String> mErrMsg;
    private HashMap<String,MutableLiveData<List<NoteBean>>> mNoteMap;


    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRep = NoteRepository.getInstance(application.getApplicationContext());
        mLoginRep = LoginRepository.getInstance();
        mNoteMap = new HashMap<>();
        mErrMsg = new MutableLiveData<>();
    }

    public void initListener(){
        mNoteRep.setQueryListener(new NoteRepository.QueryByKindListener() {
            @Override
            public void onSuccess(List<NoteBean> result, String kind) {
                MutableLiveData<List<NoteBean>> data = mNoteMap.get(kind);
                String s = NoteViewModel.this.toString();
                //按创建时间倒叙排列
                Collections.reverse(result);
                if (data.getValue() == null || !data.getValue().equals(result)){
                    data.setValue(result);
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
        if (!mNoteMap.containsKey("全部")){
            mNoteMap.put("全部", new MutableLiveData<>());
        }
        mNoteRep.queryNotes(mLoginRep.getCurrentUser());
    }

    public void queryNoteByKind(String kind){
        if (!mNoteMap.containsKey(kind)){
            mNoteMap.put(kind, new MutableLiveData<>());
        }
        mNoteRep.queryNotesByKind(mLoginRep.getCurrentUser(), kind);
    }

    public void deleteNote(NoteBean note){
        mNoteRep.deleteNote(note);
    }

    public void deleteNoteByKind(String kind){
        mNoteRep.deleteNoteByKind(mLoginRep.getCurrentUser().getUserId(), kind);
    }

    public void createNoteList(String kind){
        if (!mNoteMap.containsKey(kind)){
            mNoteMap.put(kind, new MutableLiveData<>());
        }
    }

    //只更新list的值，不更新liveData的值
    public void addFirstNote(NoteBean note, String kind){
        if (mNoteMap.get(kind).getValue() == null){
            mNoteMap.get(kind).setValue(new ArrayList<>());
        }
        mNoteMap.get(kind).getValue().add(0,note);
    }

    public void updateLocalNote(NoteBean note, int index, String kind){
        mNoteMap.get(kind).getValue().remove(index);
        mNoteMap.get(kind).getValue().add(index, note);
    }

    public void deleteLocalNote(int index, String kind){
        mNoteMap.get(kind).getValue().remove(index);
    }
//
//    public MutableLiveData<List<NoteBean>> getNoteList() {
//        return mNoteList;
//    }


    public HashMap<String, MutableLiveData<List<NoteBean>>> getmNoteMap() {
        return mNoteMap;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

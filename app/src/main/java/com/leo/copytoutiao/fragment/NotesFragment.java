package com.leo.copytoutiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.utils.Utils;
import com.leo.copytoutiao.view.adapter.NotesRecyclerAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tk on 2021/4/11
 */
public class    NotesFragment extends BaseFragment {

    private static final int REFRESH_TIME = 10 * 1000;
    private FragmentNotesBinding mBinding;
    private NoteViewModel mViewModel;
    private NotesRecyclerAdapter mAdapter;
    private String mKind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,  getLayoutRes(), container, false);
        mViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(NoteViewModel.class);
        mKind = getArguments().getString("kind");
        initView();
        initObserve();
        return mBinding.getRoot();
    }


    public static NotesFragment getInstance(String kind){
        NotesFragment fragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void initView(){
        setRefreshListener();
        //初始化RecyclerView
        List<NoteBean> noteBeans = new ArrayList<>();
        if (mViewModel.getNoteList().getValue() != null){
            noteBeans.addAll(mViewModel.getNoteList().getValue());
        }
        mAdapter = new NotesRecyclerAdapter(noteBeans, R.layout.item_note_text, R.layout.item_note_text_pic);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void initObserve(){
        mViewModel.getNoteList().observe(this, new Observer<List<NoteBean>>() {
            @Override
            public void onChanged(List<NoteBean> noteBeans) {
                mAdapter.getData().clear();
                mAdapter.getData().addAll(noteBeans);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_notes;
    }

    private void setRefreshListener() {
        mBinding.refresh.setOnRefreshListener(refresh -> {
            //isRequest = true;
            // 网络请求
            //method
            mBinding.refresh.finishRefresh(REFRESH_TIME);
        });
        //不允许下拉加载更多
        mBinding.refresh.setEnableLoadmore(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EditActivity.CREATE_NOTE:
                if (data != null){
                    NoteBean note = (NoteBean) data.getSerializableExtra("note");
                    if (note.getKind().equals(mKind)){
                        //在头部新增一项
                        //更新vm的值
                        mAdapter.getData().add(0,note);
                        mAdapter.notifyItemInserted(0);
                        mViewModel.addFirstNote(note);
                    }
                }
                break;
            case EditActivity.Edit_NOTE:
                if (data != null){
                    NoteBean note = (NoteBean) data.getSerializableExtra("note");
                    if (note.getKind().equals(mKind)){
                        //修改中间的某项
                        //更改vm的值
                        //index 下次再想办法携带过来，暂时先用0替代
                        mAdapter.notifyItemChanged(0);
                        mViewModel.updateLocalNote(note,0);
                    }
                }
                break;
        }
    }
}

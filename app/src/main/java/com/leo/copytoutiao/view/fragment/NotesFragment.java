package com.leo.copytoutiao.view.fragment;

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

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.view.activity.EditActivity;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.view.adapter.NotesRecyclerAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tk on 2021/4/11
 */
public class NotesFragment extends BaseFragment {

    private static final int REFRESH_TIME = 10 * 1000;
    private FragmentNotesBinding mBinding;
    private NoteViewModel mViewModel;
    private NotesRecyclerAdapter mAdapter;
    private String mKind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        mKind = getArguments().getString("kind");
        mViewModel = new ViewModelProvider(getActivity(),
                new ViewModelProvider.AndroidViewModelFactory(getActivity()
                        .getApplication()))
                .get(NoteViewModel.class);
        initView();
        mViewModel.queryNoteByKind(mKind);
        initObserve();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public static NotesFragment getInstance(String kind) {
        NotesFragment fragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void initView() {
        setRefreshListener();
        //?????????RecyclerView
        List<NoteBean> noteBeans = new ArrayList<>();
//        if (mViewModel.getmNoteMap().containsKey(mKind) && mViewModel.getmNoteMap().get(mKind).getValue() != null) {
//            noteBeans.addAll(mViewModel.getmNoteMap().get(mKind).getValue());
//        }
        mAdapter = new NotesRecyclerAdapter(noteBeans, R.layout.item_note_text, R.layout.item_note_text_pic);
        mAdapter.setOnClickListener((bean, position) -> {
            EditActivity.startActivityForResult(getParentFragment(), bean, position);
        });
        mAdapter.setDeleteListener((note, position) -> {
            mViewModel.deleteNote(note);
            ((MainNoteFragment) getParentFragment()).deleteItem(note, position);
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void initObserve() {
        mViewModel.getmNoteMap().get(mKind).observe(this, new Observer<List<NoteBean>>() {
            @Override
            public void onChanged(List<NoteBean> noteBeans) {
                if (mKind.equals("??????")) {
                    if (noteBeans == null){
                        Log.d("sakura", "null");
                    }else {
                        Log.d("sakura",noteBeans.size()+"");
                    }
                }
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
            // ????????????
            //method
            mBinding.refresh.finishRefresh(REFRESH_TIME);
        });
        //???????????????????????????
        mBinding.refresh.setEnableLoadmore(false);
        mBinding.refresh.setEnableRefresh(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("sakura", "result-->" + mKind);
        switch (requestCode) {
            case EditActivity.CREATE_NOTE:
                if (data != null) {
                    NoteBean note = (NoteBean) data.getSerializableExtra("note");
                    if (note.getKind().equals(mKind) || mKind.equals("??????")) {
                        //?????????????????????
                        //??????vm??????
                        mAdapter.getData().add(0, note);
                        mAdapter.notifyItemInserted(0);
                        mBinding.recyclerView.scrollToPosition(0);
                        mViewModel.addFirstNote(note, mKind);
                    }
                }
                break;
            case EditActivity.Edit_NOTE:
                if (data != null) {
                    NoteBean note = (NoteBean) data.getSerializableExtra("note");
                    int position = data.getIntExtra("position", -1);
                    //??????????????????????????????
                    //???????????????????????????
                    //kind?????????????????????????????????
                    //kind?????????????????????????????????????????????
                    if (mKind.equals("??????")) {
                        //?????????????????????
                        //??????vm??????
                        if (position >= 0) {
                            mAdapter.getData().remove(position);
                            mAdapter.getData().add(position, note);
                            mAdapter.notifyItemChanged(position);
                            mViewModel.updateLocalNote(note, position, mKind);
                        }
                    } else if (mKind.equals(note.getKind())) {
                        //kind????????????
                        boolean isNew = true; //??????????????????????????????????????????
                        for (NoteBean bean : mAdapter.getData()) {
                            if (bean.getId().equals(note.getId())) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            //??????????????????????????????????????????????????????????????????????????????
                            mAdapter.getData().add(0, note);
                            mAdapter.notifyItemInserted(0);
                            mBinding.recyclerView.scrollToPosition(0);
                            mViewModel.addFirstNote(note, mKind);
                        } else {
                            //?????????????????????
                            if (position >= 0) {
                                mAdapter.getData().remove(position);
                                mAdapter.getData().add(position, note);
                                mAdapter.notifyItemChanged(position);
                                mViewModel.updateLocalNote(note, position, mKind);
                            }
                        }

                    } else {
                        //??????????????????
                        boolean isNew = true; //??????????????????????????????????????????
                        for (NoteBean bean : mAdapter.getData()) {
                            if (bean.getId() == note.getId()) {
                                isNew = false;
                                break;
                            }
                        }
                        if (!isNew) {
                            //??????????????????????????????????????????
                            mAdapter.getData().remove(position);
                            mAdapter.notifyItemRemoved(position);
                            //??????item???position
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getData().size());
                            mViewModel.deleteLocalNote(position, mKind);
                        }
                    }
                }
                break;
        }

    }

    public void deleteItem(NoteBean note, int position) {
        int index = -1;
        if (mKind.equals(note.getKind())) {
            index = position;
        } else if (mKind.equals("??????")) {
            for (NoteBean bean : mAdapter.getData()) {
                if (bean.getId() == note.getId()) {
                    index = position;
                    break;
                }
            }
        }
        if (index >= 0) {
            mAdapter.getData().remove(index);
            mAdapter.notifyItemRemoved(index);
            //??????item???position
            mAdapter.notifyItemRangeChanged(index, mAdapter.getData().size());
            mViewModel.deleteLocalNote(index, mKind);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("sakura", mKind);
    }
}

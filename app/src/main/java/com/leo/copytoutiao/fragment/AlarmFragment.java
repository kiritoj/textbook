package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.databinding.FragmentAlarmBinding;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.view.adapter.AlarmNotesRecyclerAdapter;
import com.leo.copytoutiao.view.adapter.NotesRecyclerAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseFragment;
import com.leo.copytoutiao.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmFragment extends BaseFragment{
    private FragmentAlarmBinding mBinding;
    private NoteViewModel mViewModel;
    private AlarmNotesRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        mViewModel = new ViewModelProvider(getActivity(),
                new ViewModelProvider.AndroidViewModelFactory(getActivity()
                        .getApplication()))
                .get(NoteViewModel.class);
        mViewModel.queryAlarmNotes();
        initView();
        observe();
        return mBinding.getRoot();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_alarm;
    }

    public void initView(){
        List<NoteBean> noteBeans = new ArrayList<>();
        mAdapter = new AlarmNotesRecyclerAdapter(noteBeans, R.layout.item_note_text_alarm, R.layout.item_note_text_pic_alarm);
//      暂时不提供搜索删除
//        mAdapter.setDeleteListener((note, position) -> {
//            ((MainNoteFragment)getParentFragment()).deleteItem(note, position);
//        });
        mAdapter.setOnClickListener((bean, position) -> {
            EditActivity.startActivityForResult(this, bean, position);
        });
        mAdapter.setDeleteListener(new NotesRecyclerAdapter.OnItemDeleteListener() {
            @Override
            public void deleteItem(NoteBean note, int position) {
                //取消提醒
                mAdapter.getData().remove(position);
                mAdapter.notifyItemRemoved(position);
                //更新item的position
                mAdapter.notifyItemRangeChanged(position, mAdapter.getData().size());
                mViewModel.deleteAlarmNote(note, position);
            }
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void observe(){
        mViewModel.getmAlarmNotes().observe(this, new Observer<List<NoteBean>>() {
            @Override
            public void onChanged(List<NoteBean> noteBeans) {
                if (noteBeans == null || noteBeans.size() == 0){
                    mBinding.recyclerView.setVisibility(View.INVISIBLE);
                    mBinding.empty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerView.setVisibility(View.VISIBLE);
                    mAdapter.getData().clear();
                    mAdapter.getData().addAll(noteBeans);
                    mAdapter.notifyDataSetChanged();
                    mBinding.empty.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.queryAlarmNotes();
    }
}

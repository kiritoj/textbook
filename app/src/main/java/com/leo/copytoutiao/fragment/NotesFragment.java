package com.leo.copytoutiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
import com.taoke.base.BaseRecyclerAdapter;

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
        mViewModel.initListener();
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
        //初始化RecyclerView
        List<NoteBean> noteBeans = new ArrayList<>();
        if (mViewModel.getmNoteMap().containsKey(mKind) && mViewModel.getmNoteMap().get(mKind).getValue() != null) {
            noteBeans.addAll(mViewModel.getmNoteMap().get(mKind).getValue());

        }
        mAdapter = new NotesRecyclerAdapter(noteBeans, R.layout.item_note_text, R.layout.item_note_text_pic);
        mAdapter.setOnClickListener((bean, position) -> {
            EditActivity.startActivityForResult(getParentFragment(), bean, position);
        });
        mAdapter.setDeleteListener((note, position) -> {
            ((MainNoteFragment)getParentFragment()).deleteItem(note, position);
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void initObserve() {
        mViewModel.getmNoteMap().get(mKind).observe(this, new Observer<List<NoteBean>>() {
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
        Log.d("sakura","result-->"+mKind);
        switch (requestCode) {
            case EditActivity.CREATE_NOTE:
                if (data != null) {
                    NoteBean note = (NoteBean) data.getSerializableExtra("note");
                    if (note.getKind().equals(mKind) || mKind.equals("全部")) {
                        //在头部新增一项
                        //更新vm的值
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
                    //对不同的分页进行处理
                    //全部页面：只做更新
                    //kind匹配页面，更新或者插入
                    //kind不匹配亚眠。什么都不做或者删除
                    if (mKind.equals("全部")) {
                        //修改中间的某项
                        //更改vm的值
                        if (position >= 0) {
                            mAdapter.getData().remove(position);
                            mAdapter.getData().add(position, note);
                            mAdapter.notifyItemChanged(position);
                            mViewModel.updateLocalNote(note, position, mKind);
                        }
                    } else if (mKind.equals(note.getKind())) {
                        //kind匹配界面
                        boolean isNew = true; //是否是当前分类中不存在的笔记
                        for (NoteBean bean : mAdapter.getData()) {
                            if (bean.getId() == note.getId()) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            //该笔记是从其他分类改过来的，需要在对应分类新加一条。
                            mAdapter.getData().add(0, note);
                            mAdapter.notifyItemInserted(0);
                            mBinding.recyclerView.scrollToPosition(0);
                            mViewModel.addFirstNote(note, mKind);
                        } else {
                            //没有更改就更新
                            if (position >= 0) {
                                mAdapter.getData().remove(position);
                                mAdapter.getData().add(position, note);
                                mAdapter.notifyItemChanged(position);
                                mViewModel.updateLocalNote(note, position, mKind);
                            }
                        }

                    } else {
                        //不匹配的分类
                        boolean isNew = true; //是否是当前分类中不存在的笔记
                        for (NoteBean bean : mAdapter.getData()) {
                            if (bean.getId() == note.getId()) {
                                isNew = false;
                                break;
                            }
                        }
                        if (!isNew){
                            //从这个分类出去后，删除这一项
                            mAdapter.getData().remove(position);
                            mAdapter.notifyItemRemoved(position);
                            //更新item的position
                            mAdapter.notifyItemRangeChanged(position,mAdapter.getData().size());
                            mViewModel.deleteLocalNote(position, mKind);
                        }
                    }
                }
                break;
        }

    }

    public void deleteItem(NoteBean note, int position){
        int index = -1;
        if (mKind.equals(note.getKind())){
            index = position;
        } else if (mKind.equals("全部")){
            for (NoteBean bean : mAdapter.getData()){
                if (bean.getId() == note.getId()){
                    index = position;
                    break;
                }
            }
        }
        if (index >= 0){
            mAdapter.getData().remove(index);
            mAdapter.notifyItemRemoved(index);
            //更新item的position
            mAdapter.notifyItemRangeChanged(index,mAdapter.getData().size());
            mViewModel.deleteLocalNote(index, mKind);
            mViewModel.deleteNote(note);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("sakura",mKind);
    }
}

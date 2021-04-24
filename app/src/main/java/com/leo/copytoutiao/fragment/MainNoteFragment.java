package com.leo.copytoutiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.activity.FolderActivity;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.repository.FolderRepository;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.operators.observable.ObservableElementAt;

/**
 * Created by tk on 2021/4/11
 */
public class MainNoteFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMainNoteBinding mBinding;
    private NoteViewModel mViewModel;
    private ArrayList<String> mTitles;
    private List<Fragment> fragments;
    private BaseFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        mViewModel = new ViewModelProvider(getActivity(),
                new ViewModelProvider.AndroidViewModelFactory(getActivity()
                        .getApplication()))
                .get(NoteViewModel.class);
        mBinding.setOnClickListener(this);
        mTitles.add("全部");
        mTitles.add("工作");
        mTitles.add("随机");
        mTitles.add("待办");
        initView();
        return mBinding.getRoot();
    }

    public void initView() {
        initViewPager();
    }

    public void initViewPager(){
        fragments = new ArrayList<>();
        for (String kind : mTitles) {
            fragments.add(NotesFragment.getInstance(kind));
        }
        adapter = new BaseFragmentAdapter(getChildFragmentManager(),mTitles,fragments);
        mBinding.viewpager.setAdapter(adapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main_note;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_button:
                int index = mBinding.viewpager.getCurrentItem();
                String kind = index == 0 ? mTitles.get(0) : mTitles.get(index);
                EditActivity.startActivityForResult(this,kind);
                break;
            case R.id.fold_manage:
                FolderActivity.startActivityForResult((AppCompatActivity) getActivity(), null, FolderActivity.REQUEST_CODE, FolderActivity.Type.manageKind);
                break;
        }
    }

    public void deleteItem(NoteBean note, int position){
        FragmentManager manager = getChildFragmentManager();
        //传递到第二层的笔记fragment中
        for(Fragment fragment : manager.getFragments()){
            ((NotesFragment)fragment).deleteItem(note, position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("sakura", "MainFragment");
        if (requestCode == FolderActivity.REQUEST_CODE && resultCode == FolderActivity.Type.manageKind){
            if (data != null){
                List<String> removeFolders = data.getStringArrayListExtra("remove");
                String addFolderName = data.getStringExtra("add");
                for (String name : removeFolders){
                    int index = mTitles.indexOf(name);
                    mTitles.remove(name);
                    fragments.remove(index);
                    mViewModel.deleteNoteByKind(name);
                }
                if (!TextUtils.isEmpty(addFolderName)){
                    mTitles.add(addFolderName);
                    fragments.add(NotesFragment.getInstance(addFolderName));
                }
                adapter.notifyDataSetChanged();
            }
        }
        FragmentManager manager = getChildFragmentManager();
        //传递到第二层的笔记fragment中
        for(Fragment fragment : manager.getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

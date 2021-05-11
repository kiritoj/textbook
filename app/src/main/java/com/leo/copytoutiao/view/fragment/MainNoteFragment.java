package com.leo.copytoutiao.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.view.activity.EditActivity;
import com.leo.copytoutiao.view.activity.FolderActivity;
import com.leo.copytoutiao.view.activity.SearchActivity;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tk on 2021/4/11
 */
public class MainNoteFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMainNoteBinding mBinding;
    private NoteViewModel mViewModel;
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
        mViewModel.queryAllFolders();
        mBinding.setOnClickListener(this);
        initView();
        observe();
        return mBinding.getRoot();
    }

    public void initView() {
        initViewPager();
    }

    public void initViewPager(){
        List<String> title = new ArrayList<>();
        if (mViewModel.getFolders().getValue() != null) {
            //手动添加一个全部分类，这个分类并存在local和remote数据中
            title.add("全部");
            for (FolderBean bean : mViewModel.getFolders().getValue()){
                title.add(bean.getName());
            }
        }
        fragments = new ArrayList<>();
        for (String kind : title) {
            fragments.add(NotesFragment.getInstance(kind));
        }
        adapter = new BaseFragmentAdapter(getChildFragmentManager(),title.size(), title,fragments);
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
                //第一个分类永远是全部，默认新笔记是第二个分类
                String kind = index == 0 ? adapter.getTitles().get(1) : adapter.getTitles().get(index);
                EditActivity.startActivityForResult(this,kind);
                break;
            case R.id.fold_manage:
                FolderActivity.startActivityForResult(this, null, FolderActivity.REQUEST_CODE, FolderActivity.Type.manageKind);
                break;
            case R.id.search_contain:
                SearchActivity.startActivity(getContext());
        }
    }

    public void deleteItem(NoteBean note, int position){
        FragmentManager manager = getChildFragmentManager();
        //传递到第二层的笔记fragment中
        for(Fragment fragment : manager.getFragments()){
            ((NotesFragment)fragment).deleteItem(note, position);
        }
    }

    public void observe(){
        mViewModel.getFolders().observe(this, new Observer<List<FolderBean>>() {
            @Override
            public void onChanged(List<FolderBean> folderBeans) {
                adapter.getTitles().clear();
                fragments.clear();
                adapter.getTitles().add("全部");
                fragments.add(NotesFragment.getInstance("全部"));
                for (FolderBean bean : folderBeans){
                    adapter.getTitles().add(bean.getName());
                    fragments.add(NotesFragment.getInstance(bean.getName()));
                }
                adapter.notifyDataSetChanged();
            }
        });
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
                    int index = adapter.getTitles().indexOf(name);
                    adapter.getTitles().remove(name);
                    fragments.remove(index);
                    mViewModel.deleteNoteByKind(name);
                }
                if (!TextUtils.isEmpty(addFolderName)){
                    adapter.getTitles().add(addFolderName);
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

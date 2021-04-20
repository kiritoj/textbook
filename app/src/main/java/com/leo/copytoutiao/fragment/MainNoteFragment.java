package com.leo.copytoutiao.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.operators.observable.ObservableElementAt;

/**
 * Created by tk on 2021/4/11
 */
public class MainNoteFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMainNoteBinding mBinding;
    String[] mTitles = {"全部","工作","随机","待办"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        mBinding.setOnClickListener(this);
        initView();
        return mBinding.getRoot();
    }

    public void initView() {
        initViewPager();
    }

    public void initViewPager(){
        List<Fragment> fragments = new ArrayList<>();
        for (String kind : mTitles) {
            fragments.add(NotesFragment.getInstance(kind));
        }
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getChildFragmentManager(),mTitles.length,mTitles,fragments);
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
                String kind = index == 0 ? mTitles[1] : mTitles[index];
                EditActivity.startActivityForResult(this,kind);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager manager = getChildFragmentManager();
        //传递到第二层的笔记fragment中
        for(Fragment fragment : manager.getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

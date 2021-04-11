package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.taoke.base.BaseFragment;

/**
 * Created by tk on 2021/4/11
 */
public class MainNoteFragment extends BaseFragment {
    private FragmentMainNoteBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        initView();
        return mBinding.getRoot();
    }

    public void initView() {
        initViewPager();
    }

    public void initViewPager(){
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main_note;
    }
}

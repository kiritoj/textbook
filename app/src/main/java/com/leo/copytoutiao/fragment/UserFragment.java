package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.FragmentUserBinding;
import com.taoke.base.BaseFragment;

/**
 * Created by tk on 2021/4/11
 */
public class UserFragment extends BaseFragment {
    private FragmentUserBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        return mBinding.getRoot();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_user;
    }
}

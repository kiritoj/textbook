package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.LoginActivity;
import com.leo.copytoutiao.databinding.FragmentUserBinding;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.taoke.base.BaseFragment;

import cn.leancloud.AVCloud;
import cn.leancloud.AVUser;

/**
 * Created by tk on 2021/4/11
 */
public class UserFragment extends BaseFragment implements View.OnClickListener{
    private FragmentUserBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        mBinding.username.setText(LoginRepository.getInstance(getActivity().getApplicationContext()).getCurrentUser().getName());
        mBinding.logout.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_user;
    }

    @Override
    public void onClick(View v) {
        AVUser.logOut();
        LoginActivity.startActivity(getContext(),null);
        getActivity().finish();
    }
}

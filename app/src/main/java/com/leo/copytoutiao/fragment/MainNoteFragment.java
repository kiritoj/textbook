package com.leo.copytoutiao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.databinding.FragmentMainNoteBinding;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tk on 2021/4/11
 */
public class MainNoteFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMainNoteBinding mBinding;

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
        String[] titles = {"全部","工作","随机","待办"};
        for (String kind : titles) {
            fragments.add(NotesFragment.getInstance(kind));
        }
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getChildFragmentManager(),titles.length,titles,fragments);
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
                startActivity(new Intent(getActivity(), EditActivity.class));
                break;
        }
    }
}

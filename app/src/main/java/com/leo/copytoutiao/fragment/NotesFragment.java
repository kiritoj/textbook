package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.taoke.base.BaseFragment;

/**
 * Created by tk on 2021/4/11
 */
class NotesFragment extends BaseFragment {

    private FragmentNotesBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,  getLayoutRes(), container, false);
        return mBinding.getRoot();
    }

    public static NotesFragment getInstance(String kind){
        NotesFragment fragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_notes;
    }
}

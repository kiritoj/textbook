package com.leo.copytoutiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.FragmentNotesBinding;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.utils.Utils;
import com.leo.copytoutiao.view.adapter.NotesRecyclerAdapter;
import com.taoke.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tk on 2021/4/11
 */
public class NotesFragment extends BaseFragment {

    private static final int REFRESH_TIME = 10 * 1000;
    private FragmentNotesBinding mBinding;
    private NotesRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,  getLayoutRes(), container, false);
        initView();
        return mBinding.getRoot();
    }


    public static NotesFragment getInstance(String kind){
        NotesFragment fragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void initView(){
        setRefreshListener();
        //初始化RecyclerView
        List<NoteBean> noteBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            noteBeans.add(new NoteBean("我是标题","我是内容",null, Utils.getCurrentTime()));
            noteBeans.add(new NoteBean("我是标题","我是内容","http://ww1.sinaimg.cn/large/006nwaiFly1g63riz9882j30lp0mmwjs.jpg", Utils.getCurrentTime()));
        }
        mAdapter = new NotesRecyclerAdapter(noteBeans, R.layout.item_note_text, R.layout.item_note_text_pic);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
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
}

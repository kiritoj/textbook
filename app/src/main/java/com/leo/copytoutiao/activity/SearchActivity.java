package com.leo.copytoutiao.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.SearchView;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivitySearchBinding;
import com.leo.copytoutiao.fragment.MainNoteFragment;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.view.adapter.NotesRecyclerAdapter;
import com.leo.copytoutiao.viewmodel.FolderViewModel;
import com.leo.copytoutiao.viewmodel.SearchViewModel;
import com.taoke.base.BaseActivity;
import com.taoke.base.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding mBinding;
    private NotesRecyclerAdapter mAdapter;
    private SearchViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(SearchViewModel.class);
        initToolBar(mBinding.toolbar,null,true,-1);
        initView();
        initObserve();
    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public void initView(){
        List<NoteBean> noteBeans = new ArrayList<>();
        if (mViewModel.getNotes().getValue() != null){
            noteBeans.addAll(mViewModel.getNotes().getValue());
        }
        mAdapter = new NotesRecyclerAdapter(noteBeans, R.layout.item_note_text, R.layout.item_note_text_pic);
//      暂时不提供搜索删除
//        mAdapter.setDeleteListener((note, position) -> {
//            ((MainNoteFragment)getParentFragment()).deleteItem(note, position);
//        });
        mAdapter.setOnClickListener((bean, position) -> {
            EditActivity.startActivityForResult(this, bean, position);
        });
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(mAdapter);

        //输入搜索监听
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    mViewModel.search(s.toString());
                }else{
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void initObserve(){
        mViewModel.getNotes().observe(this, noteBeans -> {
            mAdapter.getData().clear();
            mAdapter.getData().addAll(noteBeans);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditActivity.Edit_NOTE){
            if (data != null){
                int position = data.getIntExtra("position", -1);
                NoteBean bean = (NoteBean) data.getSerializableExtra("note");
                if (position >= 0){
                    mAdapter.getData().remove(position);
                    mAdapter.getData().add(position, bean);
                    mAdapter.notifyItemChanged(position);
                }
            }
        }
    }
}
package com.leo.copytoutiao.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityHomeBinding;
import com.leo.copytoutiao.fragment.MainNoteFragment;
import com.leo.copytoutiao.fragment.UserFragment;
import com.leo.copytoutiao.service.AlarmService;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding mBinding;
    private NoteViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(NoteViewModel.class);
        //在activity中预拉取数据，fragment直接使用
        mViewModel.queryNotes();
        Intent intent = new Intent(HomeActivity.this, AlarmService.class);
        startService(intent);
        initViews();
        initListener();
    }

    public void initViews(){
        initViewpager();
    }

    public void initViewpager(){
        mBinding.viewpager.setScroll(false);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainNoteFragment());
        fragments.add(new UserFragment());
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(), 2,null,fragments);
        mBinding.viewpager.setAdapter(adapter);
    }



    public void initListener(){
        mBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.r_button_1:
                        mBinding.viewpager.setCurrentItem(0);
                        break;
                    case R.id.r_button_2:
                        //mBinding.viewpager.setCurrentItem(1);
                        Log.d("sakura","选中中间");
                        break;
                    case R.id.r_button_3:
                        mBinding.viewpager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
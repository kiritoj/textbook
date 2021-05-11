package com.leo.copytoutiao.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityHomeBinding;
import com.leo.copytoutiao.view.fragment.AlarmFragment;
import com.leo.copytoutiao.view.fragment.MainNoteFragment;
import com.leo.copytoutiao.view.fragment.UserFragment;
import com.leo.copytoutiao.service.AlarmService;
import com.leo.copytoutiao.view.adapter.BaseFragmentAdapter;
import com.leo.copytoutiao.viewmodel.NoteViewModel;

import org.jetbrains.annotations.NotNull;

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
        fragments.add(new AlarmFragment());
        fragments.add(new UserFragment());
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(), 3,null,fragments);
        mBinding.viewpager.setAdapter(adapter);
    }



    public void initListener(){
        mBinding.nav.setItemIconTintList(null);
        mBinding.nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        mBinding.viewpager.setCurrentItem(0);
                        break;
                    case R.id.navigation_alarm:
                        mBinding.viewpager.setCurrentItem(1);
                        break;
                    case R.id.navigation_mine:
                        mBinding.viewpager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

}
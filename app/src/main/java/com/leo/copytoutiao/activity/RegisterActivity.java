package com.leo.copytoutiao.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.copytoutiao.R;

import com.leo.copytoutiao.databinding.ActivityRegisterBinding;
import com.leo.copytoutiao.viewmodel.FolderViewModel;
import com.leo.copytoutiao.viewmodel.RegisterViewModel;
import com.taoke.base.BaseActivity;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private ActivityRegisterBinding mBinding;
    private RegisterViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(RegisterViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mBinding.setOnClickListener(this);
        initToolBar(findViewById(R.id.toolbar), "注册账号", false, -1);
        initView();
        observe();

    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    public void initView(){
        //键盘右下角登录
        mBinding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mViewModel.register(mBinding.username.getText().toString(),
                            mBinding.password.getText().toString());
                }
                return false;
            }
        });

        mBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(mBinding.username.getText())){
                    mBinding.register.setEnabled(false);
                } else {
                    mBinding.register.setEnabled(true);
                }
            }
        });

        mBinding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(mBinding.password.getText())) {
                    mBinding.register.setEnabled(false);
                } else {
                    mBinding.register.setEnabled(true);
                }
            }
        });
    }

    public void observe(){
        mViewModel.getIsLoginIng().observe(this, aBoolean -> {
            if (aBoolean) {
                //显示进度，login不可用
                mBinding.loading.setVisibility(View.VISIBLE);
                mBinding.register.setEnabled(false);
            } else {
                mBinding.loading.setVisibility(View.GONE);
                mBinding.register.setEnabled(true);
            }
        });

        mViewModel.getRegisterResult().observe(this, s -> {
            if (s.equals("OK")){
                //注册成功，跳转至登录页
                LoginActivity.startActivity(this, mBinding.username.getText().toString());
            } else {
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            mViewModel.register(mBinding.username.getText().toString(),
                    mBinding.password.getText().toString());
        }
    }
}
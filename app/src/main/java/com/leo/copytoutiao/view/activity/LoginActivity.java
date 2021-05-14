package com.leo.copytoutiao.view.activity;

import androidx.databinding.DataBindingUtil;
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
import com.leo.copytoutiao.databinding.ActivityLoginBinding;
import com.leo.copytoutiao.utils.NetworkUtils;
import com.leo.copytoutiao.viewmodel.LoginViewModel;
import com.taoke.base.BaseActivity;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private LoginViewModel mLoginViewModel;
    private ActivityLoginBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(LoginViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setOnClickListener(this);
        if (!NetworkUtils.isConnected(this)){
            //网络不可用直接跳转至主页
            mLoginViewModel.initLocalUser();
            HomeActivity.startActivity(this);
            finish();
        }
        if (mLoginViewModel.checkHasLogin()){
            HomeActivity.startActivity(this);
            finish();
        }
        initToolBar(findViewById(R.id.toolbar), "备忘录", false, -1);
        initView();
        observe();
    }

    public static void startActivity(Context context, String userName){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("name",userName);
        context.startActivity(intent);
    }

    public void initView(){
        //键盘右下角登录
        mBinding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mLoginViewModel.login(mBinding.username.getText().toString(),
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
                    mBinding.login.setEnabled(false);
                } else {
                    mBinding.login.setEnabled(true);
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
                    mBinding.login.setEnabled(false);
                } else {
                    mBinding.login.setEnabled(true);
                }
            }
        });
    }

    public void observe(){
        mLoginViewModel.getIsLoginIng().observe(this, aBoolean -> {
            if (aBoolean) {
                //显示进度，login不可用
                mBinding.loading.setVisibility(View.VISIBLE);
                mBinding.login.setEnabled(false);
            } else {
                mBinding.loading.setVisibility(View.GONE);
                mBinding.login.setEnabled(true);
            }
        });

        mLoginViewModel.getLoginResult().observe(this, s -> {
            if ("OK".equals(s)){
                //注册成功，跳转至首页
                HomeActivity.startActivity(this);
                finish();
            } else {
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                mLoginViewModel.login(mBinding.username.getText().toString(),
                        mBinding.password.getText().toString());
                break;
            case R.id.register:
                RegisterActivity.startActivity(this);
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null){
            mBinding.username.setText(intent.getStringExtra("name"));
            mBinding.password.setText("");
        }
    }
}
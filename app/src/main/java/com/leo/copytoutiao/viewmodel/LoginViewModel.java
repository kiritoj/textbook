package com.leo.copytoutiao.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.util.Patterns;

import com.leo.copytoutiao.model.bean.UserBean;
import com.leo.copytoutiao.model.repository.FolderRepository;
import com.leo.copytoutiao.model.repository.LoginRepository;

import cn.leancloud.AVUser;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isLoginIng;
    private MutableLiveData<String> loginResult;

    private LoginRepository loginRepository;

    public LoginViewModel(Application application){
        super(application);
        isLoginIng = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
        loginRepository = LoginRepository.getInstance(application.getApplicationContext());
    }

    public void login(String name, String password){
        isLoginIng.setValue(true);
        loginRepository.login(name, password, new LoginRepository.LoginListener() {
            @Override
            public void success(AVUser user) {
                isLoginIng.setValue(false);
                loginResult.setValue("OK");
            }

            @Override
            public void error(int errorCode, String errMsg) {
                isLoginIng.setValue(false);
                loginResult.setValue(errMsg);
            }
        });
    }

    public void initLocalUser(){
        //没有网络的时候，使用上一次用户的登录信息
        loginRepository.loadLocalUser();
    }

    public boolean checkHasLogin(){
        AVUser curUser = AVUser.currentUser();
        if (curUser != null){
            loginRepository.setCurUser(new UserBean(curUser.getUsername(), curUser.getPassword()));
            return true;
        }
        return false;
    }

    public MutableLiveData<Boolean> getIsLoginIng() {
        return isLoginIng;
    }

    public MutableLiveData<String> getLoginResult() {
        return loginResult;
    }
}
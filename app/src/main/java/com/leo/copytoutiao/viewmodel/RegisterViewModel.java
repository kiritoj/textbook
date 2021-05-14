package com.leo.copytoutiao.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leo.copytoutiao.model.repository.FolderRepository;
import com.leo.copytoutiao.model.repository.LoginRepository;

import cn.leancloud.AVUser;

public class RegisterViewModel extends AndroidViewModel {

    private LoginRepository mLoginRep;
    private FolderRepository mFolderRep;
    private MutableLiveData<Boolean> isRegisterIng;
    private MutableLiveData<String> registerResult;
    private final String regex = "[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+";

    public RegisterViewModel(Application application){
        super(application);
        isRegisterIng = new MutableLiveData<>();
        registerResult = new MutableLiveData<>();
        mLoginRep = LoginRepository.getInstance(application.getApplicationContext());
        mFolderRep = FolderRepository.getInstance(application.getApplicationContext());
    }

    public void register(String name, String password){
        if (!name.matches(regex)){
            registerResult.setValue("用户名格式错误");
            return;
        }
        if (password.length() < 8){
            registerResult.setValue("密码长度低于8位");
            return;
        }
        isRegisterIng.setValue(true);
        mLoginRep.register(name, password, new LoginRepository.LoginListener() {
            @Override
            public void success(AVUser user) {
                isRegisterIng.setValue(false);
                registerResult.setValue("OK");
                //注册成功后初始化本地数据库和远程数据库，添加默认的分类
                String[] folders = {"随记","工作"};
                mFolderRep.insertFolders(user.getUsername(), folders);
            }

            @Override
            public void error(int errorCode, String errMsg) {
                isRegisterIng.setValue(false);
                registerResult.setValue(errMsg);
            }
        });
    }

    public MutableLiveData<Boolean> getIsLoginIng() {
        return isRegisterIng;
    }

    public MutableLiveData<String> getRegisterResult() {
        return registerResult;
    }
}

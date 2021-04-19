package com.leo.copytoutiao.model.repository;

import android.content.Context;

import com.leo.copytoutiao.model.bean.UserBean;
import com.taoke.base.BaseRepository;

public class LoginRepository extends BaseRepository<UserBean> {
    private UserBean curUser;
    private static volatile LoginRepository instance;

    public static LoginRepository getInstance(){
        if (instance == null){
            synchronized (LoginRepository.class){
                if (instance == null) {
                    instance = new LoginRepository();
                }
            }
        }
        return instance;
    }

    public UserBean getCurrentUser(){
        return curUser;
    }

    private LoginRepository() {}

    public void login(String name, String password){
        curUser = new UserBean(name, password);
    }

}

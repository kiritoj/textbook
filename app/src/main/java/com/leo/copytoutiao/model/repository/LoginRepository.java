package com.leo.copytoutiao.model.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.leo.copytoutiao.model.bean.UserBean;
import com.taoke.base.BaseRepository;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginRepository extends BaseRepository<UserBean> {
    private UserBean curUser;
    private static volatile LoginRepository instance;
    private final String TAG = "LoginRepository";
    private WeakReference<Context> reference;

    public static LoginRepository getInstance(Context context){
        if (instance == null){
            synchronized (LoginRepository.class){
                if (instance == null) {
                    instance = new LoginRepository(context);
                }
            }
        }
        return instance;
    }

    public interface LoginListener{
        void success(AVUser user);
        void error(int errorCode, String errMsg);
    }

    public UserBean getCurrentUser(){
        return curUser;
    }

    private LoginRepository(Context context) {
        reference = new WeakReference<>(context);
    }

    public void login(String name, String password, LoginListener listener){
        AVUser.logIn(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AVUser>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) { }

                    @Override
                    public void onNext(@NotNull AVUser avUser) {
                        //登录成功
                        if (listener != null) {
                            listener.success(avUser);
                            curUser = new UserBean(avUser.getUsername(), avUser.getPassword());
                            //保存登录信息
                            if (reference.get() != null) {
                                SharedPreferences preferences = reference.get().getSharedPreferences("user",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Username", avUser.getUsername());
                                editor.putString("Password", avUser.getPassword());
                                editor.putString("Uid",avUser.getUuid());
                                editor.apply();
                            }

                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (listener != null){
                            listener.error(0,e.getMessage());
                            Log.d(TAG,"登录失败：" + e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() { }
                });

    }

    public void register(String name, String password, LoginListener listener){
        AVUser user = new AVUser();
        user.setUsername(name);
        user.setPassword(password);
        user.signUpInBackground().subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 注册成功
                if (listener != null){
                    listener.success(user);
                }
            }
            public void onError(Throwable throwable) {
                // 注册失败（通常是因为用户名已被使用）
                if (listener != null) {
                    listener.error(0, throwable.getMessage());
                }
            }
            public void onComplete() {}
        });
    }

    public void loadLocalUser(){
        if (reference.get() != null) {
            SharedPreferences preferences = reference.get().getSharedPreferences("user",Context.MODE_PRIVATE);
            String username = preferences.getString("Username","admin");
            String password = preferences.getString("Password","123456");
            curUser = new UserBean(username, password);
        }
    }

    public void setCurUser(UserBean user) {
        this.curUser = user;
    }
}

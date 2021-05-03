package com.leo.copytoutiao;

import android.app.Application;

import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;

public class MyApplication extends Application {
    //leancloud应用信息
    public static final String APP_ID = "nrfyFf1EVKbw3u1ICHbNc5bH-MdYXbMMI";
    public static final String APP_KEY = "VhJLX65Q3zSDEvSYCzzNeQQu";
    @Override
    public void onCreate() {
        super.onCreate();
        //开启调试
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        //初始化leancloud
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
    }
}

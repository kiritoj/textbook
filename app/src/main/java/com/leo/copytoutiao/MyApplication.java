package com.leo.copytoutiao;

import android.app.Application;
import android.content.Context;

import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;

public class MyApplication extends Application {
    //leancloud应用信息
    public static final String APP_ID = "nrfyFf1EVKbw3u1ICHbNc5bH-MdYXbMMI";
    public static final String APP_KEY = "VhJLX65Q3zSDEvSYCzzNeQQu";
    private static Context context;
    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        //开启调试
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        //初始化leancloud
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
    }

    public static Context getGlobalContext(){
        if (context != null) {
            return context;
        }
        return null;
    }
}

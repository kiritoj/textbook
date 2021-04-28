package com.leo.copytoutiao.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.model.bean.NoteBean;

public class AlarmService extends Service {

    private NotificationManager notificationManager;

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //前台服务避免应用进程被杀掉，闹钟失效
        Notification notification = getNotification("life", "保活", NotificationManager.IMPORTANCE_DEFAULT,
                "记事本", "程序正在运行中", R.mipmap.ic_launcher, null);
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
            Notification notification = getNotification("alarm", "闹钟", NotificationManager.IMPORTANCE_MAX,
                    title, content, R.mipmap.ic_launcher, new long[]{0,1000,500,1000});
            notificationManager.notify(2, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Android 8.0以上创建通知渠道
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channerId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channerId, channelName, importance);
        notificationManager.createNotificationChannel(channel);
    }

    public Notification getNotification(String channelId, String channelName, int importance,
                                        String title, String content, int resId, long[] vibrate) {
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName, importance);
            notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(resId)
                    .setContentIntent(null)
                    .build();
        } else {
            //android 8.0已下不用设置渠道
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(resId)
                    .setContentIntent(null)
                    .build();
        }
        if (vibrate != null){
            notification.vibrate = vibrate;
        }
        return notification;
    }

}
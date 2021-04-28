package com.leo.copytoutiao.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class AlarmManagerUtil {
    private final AlarmManager alarmManager;
    private WeakReference<Context> reference; //弱引用防止内存泄露
    private long intervalTime = 24 * 60 * 60 * 1000; //重复提醒设置为1天
    private static volatile AlarmManagerUtil instance;
    private boolean isRepeat;

    private AlarmManagerUtil(Context context){
        reference = new WeakReference<>(context);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmManagerUtil getInstance(Context context){
        if (instance == null){
            synchronized (AlarmManagerUtil.class){
                if (instance == null){
                    instance = new AlarmManagerUtil(context);
                }
            }
        }
        return instance;
    }

    //设置闹钟
    public void addAlarm(Calendar calendar, PendingIntent pendingIntent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上设置闹钟
            if (isRepeat){
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intervalTime,pendingIntent);
            } else{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    //清除闹钟
    public void removeAlarm(Calendar calendar, PendingIntent pendingIntent){
        alarmManager.cancel(pendingIntent);
    }


    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }
}

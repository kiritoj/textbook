package com.leo.copytoutiao.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.leo.copytoutiao.R;

import org.jetbrains.annotations.NotNull;

public class BootActivity extends AppCompatActivity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            LoginActivity.startActivity(BootActivity.this,"");
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        handler.sendEmptyMessageDelayed(1,1500);
    }
}
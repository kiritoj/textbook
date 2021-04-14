package com.taoke.base;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public abstract class BaseActivity extends AppCompatActivity {

    public static final int FLAG_NOMENU = -1;
    private View.OnClickListener mlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
//        if (Build.VERSION.SDK_INT >= 21) {
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            getWindow().getDecorView().setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);

//        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initToolBar(Toolbar toolbar, String title, boolean showHomeAsUp, int iconId) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
        if (showHomeAsUp && iconId != -1) {
            getSupportActionBar().setHomeAsUpIndicator(iconId);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public int getMenuRes() {
        return FLAG_NOMENU;
    }

    ;
}
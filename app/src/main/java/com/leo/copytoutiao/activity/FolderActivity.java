package com.leo.copytoutiao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityFolderBinding;
import com.leo.copytoutiao.view.adapter.FolderRecyclerAdapter;
import com.taoke.base.BaseActivity;
import com.taoke.base.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends BaseActivity {

    public static final String SELECT_KIND = "select_kind";
    public static final int REQUEST_CODE = 10;
    public static final int RESULT_CODE = 20;
    public static final String KIND = "kind";
    private ActivityFolderBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_folder);
        initView();
        initToolBar(findViewById(R.id.toolbar), "笔记归属文件夹", true, -1);
    }

    public static void startActivity(Context context, String kind){
        Intent intent = new Intent(context,FolderActivity.class);
        intent.putExtra(KIND,kind);
        context.startActivity(intent);
    }

    public static void startActivityForResult(AppCompatActivity context, String kind, int requestCode){
        Intent intent = new Intent(context, FolderActivity.class);
        intent.putExtra(KIND, kind);
        context.startActivityForResult(intent,requestCode);
    }

    public void initView(){
        List<String> list = new ArrayList<>();
        list.add("随记");
        list.add("工作");
        list.add("待办");
        FolderRecyclerAdapter adapter = new FolderRecyclerAdapter("工作",list,R.layout.item_folder);
        adapter.setOnClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onClick(String s) {
                if (!TextUtils.isEmpty(s)){
                    Intent intent = new Intent();
                    intent.putExtra(SELECT_KIND,s);
                    setResult(RESULT_CODE, intent);
                    finish();
                }
            }
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(FolderActivity.this));
        mBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getMenuRes() {
        return R.menu.menu_folder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuRes(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.add:
                //新建类别
                final EditText editText = new EditText(FolderActivity.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(FolderActivity.this);
                inputDialog.setTitle("新建文件夹").setView(editText);
                inputDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            String kind = editText.getText().toString();
                            if (!TextUtils.isEmpty(kind)){
                                Intent intent = new Intent();
                                intent.putExtra(SELECT_KIND,kind);
                                setResult(RESULT_CODE, intent);
                                finish();
                            }
                        });
                inputDialog.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                inputDialog.show();
                break;
        }
        return true;
    }
}
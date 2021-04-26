package com.leo.copytoutiao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.leo.copytoutiao.R;
import com.leo.copytoutiao.databinding.ActivityFolderBinding;
import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.repository.LoginRepository;
import com.leo.copytoutiao.view.adapter.FolderRecyclerAdapter;
import com.leo.copytoutiao.viewmodel.FolderViewModel;
import com.leo.copytoutiao.viewmodel.NoteViewModel;
import com.taoke.base.BaseActivity;
import com.taoke.base.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends BaseActivity {

    public static final String SELECT_KIND = "select_kind";
    public static final int REQUEST_CODE = 10;
    public static final int RESULT_CODE = 20;
    public static final String KIND = "kind";
    public static final String TYPE = "type";
    private ActivityFolderBinding mBinding;
    private FolderViewModel mViewModel;
    private int type;
    private FolderRecyclerAdapter mAdapter;
    private ArrayList<String> removeFolders = new ArrayList<>();

    public interface Type{
        int selectKind = 31;//选择分类
        int manageKind = 32;//管理分类
    }

    public interface Status{
        int delete = 0;
        int add = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_folder);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(FolderViewModel.class);
        //mViewModel.queryFolder(LoginRepository.getInstance().getCurrentUser().getUserId());
        type = getIntent().getIntExtra(TYPE,Type.selectKind);
        initView();
        initObserve();
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

    public static void startActivityForResult(Fragment fragment, String kind, int requestCode, int type){
        Intent intent = new Intent(fragment.getContext(), FolderActivity.class);
        intent.putExtra(KIND, kind);
        intent.putExtra(TYPE, type);
        fragment.startActivityForResult(intent,requestCode);
    }

    public void initView(){
        List<FolderBean> data = new ArrayList<>();
        data.add(new FolderBean(123,"工作"));
        data.add(new FolderBean(123,"随机"));
        data.add(new FolderBean(123,"待办"));
        if (mViewModel.getFolders().getValue() != null){
            data.addAll(mViewModel.getFolders().getValue());
        }
        mAdapter = new FolderRecyclerAdapter("工作",data,R.layout.item_folder);
        mAdapter.setOnClickListener((bean, position) -> {
                Intent intent = new Intent();
                intent.putExtra(SELECT_KIND,bean.getName());
                setResult(Type.selectKind, intent);
                finish();

        });
        mAdapter.setDeleteListener((bean, position) -> {
            mViewModel.deleteFolder(bean);
            removeFolders.add(bean.getName());

        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(FolderActivity.this));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void initObserve(){
        mViewModel.getFolders().observe(this, folderBeans -> {
            mAdapter.getData().clear();
            mAdapter.getData().addAll(folderBeans);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public int getMenuRes() {
        return R.menu.menu_folder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (type == Type.selectKind){
            return super.onCreateOptionsMenu(menu);
        }
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
                                intent.putStringArrayListExtra("remove",removeFolders);
                                intent.putExtra("add",kind);
                                setResult(Type.manageKind, intent);
                                mViewModel.addFolder(kind);
                                finish();
                            }
                        });
                inputDialog.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                inputDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("remove",removeFolders);
        setResult(Type.manageKind ,intent);
        super.onBackPressed();
    }
}
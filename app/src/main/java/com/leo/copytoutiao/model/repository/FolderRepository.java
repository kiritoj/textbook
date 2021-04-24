package com.leo.copytoutiao.model.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leo.copytoutiao.activity.EditActivity;
import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.db.DataBaseHelper;
import com.taoke.base.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class FolderRepository extends BaseRepository<FolderBean> {
    private static volatile FolderRepository mInstance;
    private SQLiteDatabase database;

    private FolderRepository(Context context) {
        if (context != null) {
            database = new DataBaseHelper(context, "Note.db", 1).getWritableDatabase();
        }
    }

    public static FolderRepository getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FolderRepository.class) {
                if (mInstance == null) {
                    mInstance = new FolderRepository(context);
                }
            }
        }
        return mInstance;
    }

    public void insertFolder(FolderBean folder) {
        insertFolder(folder.getUserId(), folder.getName());
    }

    public void insertFolder(int userId, String name) {
        if (database != null) {
            database.execSQL("insert into folder values(?, ?)",
                    new String[]{String.valueOf(userId), name});
        }
    }


    public void deleteFolder(int userId, String name) {
        if (database != null) {
            database.execSQL("delete from folder where userid = ? and name = ?",
                    new String[]{String.valueOf(userId), name});
        }
    }

    public void queryFolder(int userId) {
        if (database != null) {
            Cursor cursor = database.rawQuery("select * from folder where userid = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                List<FolderBean> beanList = new ArrayList<>();
                do {
                    FolderBean bean = new FolderBean(cursor.getInt(cursor.getColumnIndex("userid")),
                            cursor.getString(cursor.getColumnIndex("name")));
                    beanList.add(bean);

                } while (cursor.moveToNext());
                for (LoadListener<FolderBean> listener : getListeners()){
                    listener.onSuccess(beanList);
                }
            } else {
                //数据库没有
                for (LoadListener<FolderBean> listener : getListeners()) {
                    listener.onFailed("");
                }
            }
        }
    }




}

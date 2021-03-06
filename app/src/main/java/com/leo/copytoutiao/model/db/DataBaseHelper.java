package com.leo.copytoutiao.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created by tk on 2021/4/13
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建笔记表
        final String createNote = "create table note (id text, title text, content text, url text, kind text, time bigint, username text, alarmtime bigint)";
        //用户表
        final String createUser = "create table user(name text, password text)";
        //类别表
        final String createFolder = "create table folder(username text, name text)";
        db.execSQL(createNote);
        db.execSQL(createUser);
        db.execSQL(createFolder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.leo.copytoutiao.model.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.bean.UserBean;
import com.leo.copytoutiao.model.db.DataBaseHelper;
import com.taoke.base.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository extends BaseRepository<NoteBean> {

    private static volatile NoteRepository instance;
    private SQLiteDatabase database;

    private NoteRepository(Context context){
        if (context != null) {
            database = new DataBaseHelper(context, "Note.db", 1).getWritableDatabase();
        }
    }

    public static NoteRepository getInstance(Context context){
        if (instance == null){
            synchronized (NoteRepository.class){
                if (instance == null){
                    instance = new NoteRepository(context);
                }
            }
        }
        return instance;
    }

    public void insertNote(NoteBean bean){
        if (database != null) {
            database.execSQL("insert into note (title, content, url, kind, time,userid) values (?,?,?,?,?,?)"
                    , new String[]{bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(), String.valueOf(bean.getTime()), String.valueOf(bean.getUserBean().getUserId())});
        }
    }

    public void updateNote(NoteBean bean){
        if (database != null){
            database.execSQL("update note set title = ?,content = ?, url = ?,kind = ? where time = ? and userid = ?"
                    , new String[]{bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(), String.valueOf(bean.getTime()), String.valueOf(bean.getUserBean().getUserId())});
        }
    }

    public void queryNotes(UserBean user) {
        if (database != null) {
            Cursor cursor = database.rawQuery("select * from note where userid = ?",
                    new String[]{String.valueOf(user.getUserId())});
            if (cursor.moveToFirst()) {
                List<NoteBean> noteList = new ArrayList<>();

                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("kind")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user);
                    noteList.add(note);

                } while (cursor.moveToNext());
                for (LoadListener<NoteBean> listener : getListeners()){
                    listener.onSuccess(noteList);
                }
            } else {
                //数据库没有
                for (LoadListener<NoteBean> listener : getListeners()) {
                    listener.onFailed("");
                }
            }
        }
    }


}

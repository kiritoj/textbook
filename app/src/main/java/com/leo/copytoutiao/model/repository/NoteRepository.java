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
    private QueryByKindListener queryListener;
    private static final String ALL = "全部";

    public interface QueryByKindListener{
        void onSuccess(List<NoteBean> result, String kind);
        void onFailed(String errMsg);
    }

    public void setQueryListener(QueryByKindListener listener){
        this.queryListener = listener;
    }

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
            database.execSQL("insert into note (title, content, url, kind, time,userid,alarmtime) values (?,?,?,?,?,?,?)"
                    , new String[]{bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(),
                            String.valueOf(bean.getTime()),
                            String.valueOf(bean.getUserBean().getUserId()),
                            String.valueOf(bean.getAlarmTime())});
        }
    }

    public void updateNote(NoteBean bean){
        if (database != null){
            database.execSQL("update note set title = ?,content = ?, url = ?,kind = ? where time = ? and userid = ?"
                    , new String[]{bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(), String.valueOf(bean.getTime()), String.valueOf(bean.getUserBean().getUserId())});
        }
    }

    //查询用户全部笔记
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
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")));
                    noteList.add(note);

                } while (cursor.moveToNext());
                if (queryListener != null){
                    queryListener.onSuccess(noteList, ALL);
                }
            } else {
                //数据库没有
                if (queryListener != null){
                    queryListener.onFailed("");
                }
            }
        }
    }

    //查询用户某个分类的笔记
    public void queryNotesByKind(UserBean user, String kind){
        if (ALL.equals(kind)){
            //查询全部笔记
            queryNotes(user);
        } else if (database != null) {
            Cursor cursor = database.rawQuery("select * from note where userid = ? and kind = ?",
                    new String[]{String.valueOf(user.getUserId()), kind});
            if (cursor.moveToFirst()) {
                List<NoteBean> noteList = new ArrayList<>();

                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            kind,
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")));
                    noteList.add(note);

                } while (cursor.moveToNext());
                if (queryListener != null){
                    queryListener.onSuccess(noteList, kind);
                }
            } else {
                //数据库没有
                if (queryListener != null){
                    queryListener.onFailed("");
                }
            }
        }
    }

    //删除笔记
    public void deleteNote(NoteBean bean){
        if (database != null){
            database.execSQL("delete from note where time = ? and userid = ?"
                    , new String[]{String.valueOf(bean.getTime()), String.valueOf(bean.getUserBean().getUserId())});
        }
    }

    //删除用户某个分类的全部笔记
    public void deleteNoteByKind(int userId, String kind){
        if (database != null){
            database.execSQL("delete from note where kind = ? and userid = ?"
                    , new String[]{kind, String.valueOf(userId)});
        }
    }

    //模糊搜索
    public void searchNote(String key, UserBean user, LoadListener<NoteBean> listener){
        if (database != null){
            Cursor cursor =  database.rawQuery("select * from note where title like ? or content like ?", new String[]{"%" + key + "%"});
            if (cursor.moveToFirst()){
                List<NoteBean> list = new ArrayList<>();
                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("kind")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")));
                    list.add(note);

                } while (cursor.moveToNext());
                if (listener != null){
                    listener.onSuccess(list);
                }
            } else if (listener != null){
                listener.onFailed("");
            }
        }
    }
}

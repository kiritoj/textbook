package com.leo.copytoutiao.model.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.sax.ElementListener;
import android.util.Log;

import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.bean.NoteBean;
import com.leo.copytoutiao.model.bean.UserBean;
import com.leo.copytoutiao.model.db.DataBaseHelper;
import com.taoke.base.BaseRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NoteRepository extends BaseRepository<NoteBean> {

    private static volatile NoteRepository instance;
    private SQLiteDatabase database;
    private static final String ALL = "全部";
    private static final String TAG = "NoteRepository";

    public interface QueryByKindListener{
        void onSuccess(List<NoteBean> result, String kind);
        void onFailed(String errMsg);
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

    /**
     * 新增一条笔记：Local & Remote
     * @param bean
     */
    public void insertNote(NoteBean bean){
        insertNoteToLocal(bean);
        insertNoteToRemote(bean);
    }

    /**
     * 本地新增一条笔记
     * @param bean
     */
    public void insertNoteToLocal(NoteBean bean){
        if (database != null) {
            database.execSQL("insert into note (id, title, content, url, kind, time,username,alarmtime) values (?,?,?,?,?,?,?,?)"
                    , new String[]{bean.getId(), bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(),
                            String.valueOf(bean.getTime()),
                            String.valueOf(bean.getUserBean().getName()),
                            String.valueOf(bean.getAlarmTime())});
        }
    }

    /**
     * 本溪批量新增笔记
     * @param noteBeans
     */
    public void insertNotesToLocal(List<NoteBean> noteBeans){
        if(database != null) {
//            database.beginTransaction();
            for (NoteBean bean : noteBeans) {
                database.execSQL("insert into note (id, title, content, url, kind, time,username,alarmtime) values (?,?,?,?,?,?,?,?)"
                        , new String[]{bean.getId(), bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(),
                                String.valueOf(bean.getTime()),
                                String.valueOf(bean.getUserBean().getName()),
                                String.valueOf(bean.getAlarmTime())});
            }
//            database.endTransaction();
        }
    }


    /**
     * remotei新增一条笔记
     * @param bean
     */
    public void insertNoteToRemote(NoteBean bean){
        AVObject object =  new AVObject("Note");
        object.put("id", bean.getId());
        object.put("title",bean.getTitle());
        object.put("content",bean.getContent());
        object.put("url", bean.getUrl() == null ? "" : bean.getUrl());
        object.put("kind",bean.getKind());
        object.put("time", bean.getTime());
        object.put("username",bean.getUserBean().getName());
        object.put("alarmtime",bean.getAlarmTime());
        object.saveInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                // 成功保存之后，执行其他逻辑
                Log.d(TAG,"remote保存笔记成功");
            }
            public void onError(Throwable throwable) {
                // 异常处理
                Log.d(TAG,"remote插入笔记失败：" + throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    /**
     * 更新一条笔记内容： Local & Remote
     * @param bean
     */
    public void updateNote(NoteBean bean){
        updateLocalNote(bean);
        updateRemoteNote(bean);
    }

    /**
     * 更新本地笔记内容
     * @param bean
     */
    public void updateLocalNote(NoteBean bean){
        if (database != null){
            database.execSQL("update note set title = ?,content = ?, url = ?,kind = ? where id = ?"
                    , new String[]{bean.getTitle(), bean.getContent(), bean.getUrl(), bean.getKind(), String.valueOf(bean.getId())});
        }
    }

    /**
     * 更新远程笔记内容
     * @param bean
     */
    public void updateRemoteNote(NoteBean bean){
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("id",bean.getId());
        query.getFirstInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject note) {
                note.put("title", bean.getTitle());
                note.put("content", bean.getContent());
                note.put("url", bean.getUrl());
                note.put("kind", bean.getKind());
                note.saveInBackground().subscribe(new Observer<AVObject>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull AVObject avObject) {
                        Log.d(TAG,"更新远程笔记成功：");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG,"更新远程笔记失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
            public void onError(Throwable throwable) {
                Log.d(TAG,"朝朝要更新的远程笔记失败：" + throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    /**
     * 设置提醒时间
     */
    public void setAlarmTime(NoteBean bean, long time){
        setAlarmTime(bean.getId(), time);
    }

    public void setAlarmTime(String id, long time){
        if (database != null){
            database.execSQL("update note set alarmtime = ? where id = ?"
                    , new String[]{String.valueOf(time), id});
        }
    }

    /**
     * 查询用户的全部笔记
     * @param user
     */
    public void queryNotes(UserBean user , QueryByKindListener listener) {
        if (database != null) {
            Cursor cursor = database.rawQuery("select * from note where username = ?",
                    new String[]{user.getName()});
            if (cursor.moveToFirst()) {
                List<NoteBean> noteList = new ArrayList<>();

                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("kind")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")),
                            cursor.getString(cursor.getColumnIndex("id")));
                    noteList.add(note);
                } while (cursor.moveToNext());
                if (listener != null){
                    Log.d(TAG,"local查询全部笔记：" + noteList.toString());
                    listener.onSuccess(noteList, ALL);

                }
            } else {
                //数据库没有,尝试从远程获取
                queryNotesFromRemote(user, "全部", listener);
            }
        }
    }

    /**
     * 从远程查询用户的全部笔记
     * @param user
     */
    public void queryNotesFromRemote(UserBean user, String kind, QueryByKindListener listener
    ){
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("username", user.getName());
        if (!kind.equals(ALL)){
            query.whereEqualTo("kind",kind);
        }
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> objects) {
                List<NoteBean> noteBeans = new ArrayList<>();
                for (AVObject avObject : objects){
                    noteBeans.add(new NoteBean(avObject.getString("title"),
                                avObject.getString("content"),
                                avObject.getString("url"),
                                avObject.getString("kind"),
                                avObject.getLong("time"),
                                user,
                                0,
                                avObject.getString("id")));
                }
                if (listener != null) {
                    Log.d(TAG,kind+":remote查询全部笔记：" + noteBeans.toString());
                    listener.onSuccess(noteBeans, kind);
                }
                //写回数据库
                if (!noteBeans.isEmpty())  {
                    insertNotesToLocal(noteBeans);
                }
            }
            public void onError(Throwable throwable) {
                if (listener != null){
                    listener.onFailed(throwable.getMessage());
                    Log.d(TAG,"远程查询全部笔记出现错误："+throwable.getMessage());
                }
            }
            public void onComplete() {}
        });
    }

    /**
     * 查询用户某个分类的笔记,Local & Remote
     * @param user
     * @param kind
     */
    public void queryNotesByKind(UserBean user, String kind, QueryByKindListener listener){
        if (ALL.equals(kind)){
            //查询全部笔记
            queryNotes(user, listener);
        } else if (database != null) {
            Cursor cursor = database.rawQuery("select * from note where username = ? and kind = ?",
                    new String[]{String.valueOf(user.getName()), kind});
            if (cursor.moveToFirst()) {
                List<NoteBean> noteList = new ArrayList<>();

                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            kind,
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")),
                            cursor.getString(cursor.getColumnIndex("id")));
                    noteList.add(note);

                } while (cursor.moveToNext());
                if (listener != null){
                    listener.onSuccess(noteList, kind);
                }
            } else {
                //数据库没有
                queryNotesFromRemote(user, kind, listener);
            }
        }
    }

    /**
     * 删除笔记：Local & Remote
     * @param bean
     */
    public void deleteNote(NoteBean bean){
        deleteNoteFromLocal(bean);
        deleteNoteFromRemote(bean);
    }

    /**
     * 从本地删除一条笔记
     * @param bean
     */
    public void deleteNoteFromLocal(NoteBean bean){
        if (database != null){
            database.execSQL("delete from note where id = ?"
                    , new String[]{String.valueOf(bean.getId())});
        }
    }

    /**
     * 从Remote珊瑚一条笔记
     * @param bean
     */
    public void deleteNoteFromRemote(NoteBean bean){
        Log.d(TAG,"deleteNoteFromRemote");
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("id", bean.getId());
        query.getFirstInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(AVObject note) {
                Log.d(TAG,"remote寻找要删除的笔记成功");
                note.deleteInBackground().subscribe(new Observer<AVNull>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NotNull AVNull avNull) {
                        Log.d(TAG, "remote删除一条笔记成功");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG, "remote删除一条笔记失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            public void onError(Throwable throwable) {
                Log.d(TAG,"remote寻找要删除的笔记失败：" + throwable.getMessage());
            }

            public void onComplete() {
            }
        });
    }


    /**
     * 删除用户某个分类的全部笔记;Local & Remote
     * @param userName
     * @param kind
     */
    public void deleteNoteByKind(String userName, String kind){
        deleteNoteByKindFromLocal(userName, kind);
        deleteNoteByKindFromRemote(userName, kind);
    }

    /**
     * 删除用户某个分类的全部笔记;Local
     * @param userName
     * @param kind
     */
    public void deleteNoteByKindFromLocal(String userName, String kind){
        if (database != null){
            database.execSQL("delete from note where kind = ? and username = ?"
                    , new String[]{kind, userName});
        }
    }

    /**
     * 删除用户某个分类的全部笔记;Remote
     * @param userName
     * @param kind
     */
    public void deleteNoteByKindFromRemote(String userName, String kind){
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("username", userName);
        query.whereEqualTo("kind", kind);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(List<AVObject> notes) {
                AVObject.deleteAllInBackground(notes).subscribe(new Observer<AVNull>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull AVNull avNull) {
                        Log.d(TAG,"remote删除文件夹相关笔记成功");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG,"remote删除文件夹相关笔记失败" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            public void onError(Throwable throwable) {
            }

            public void onComplete() {
            }
        });
    }

    /**
     * 模糊搜索
     * @param key
     * @param user
     * @param listener
     */
    public void searchNote(String key, UserBean user, LoadListener<NoteBean> listener){
        if (database != null){
            Cursor cursor =  database.rawQuery("select * from note where title like ? or content like ?", new String[]{"%" + key + "%","%" + key + "%"});
            if (cursor.moveToFirst()){
                List<NoteBean> list = new ArrayList<>();
                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("kind")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")),
                            cursor.getString(cursor.getColumnIndex("id")));
                    list.add(note);
                    Log.d(TAG,list.toString());
                } while (cursor.moveToNext());
                if (listener != null){
                    listener.onSuccess(list);
                }
            } else if (listener != null){
                listener.onFailed("");
            }
        }
    }

    /**
     * 查询设置闹钟的笔记
     */
    public void queryAlarmNotes(UserBean user, LoadListener<NoteBean> listener){
        if (database != null){
            Cursor cursor =  database.rawQuery("select * from note where username = ? and alarmtime > ?", new String[]{user.getName(),String.valueOf(0)});
            if (cursor.moveToFirst()){
                List<NoteBean> list = new ArrayList<>();
                do {
                    NoteBean note = new NoteBean(cursor.getString(cursor.getColumnIndex("title")),
                            cursor.getString(cursor.getColumnIndex("content")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("kind")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            user,
                            cursor.getLong(cursor.getColumnIndex("alarmtime")),
                            cursor.getString(cursor.getColumnIndex("id")));
                    list.add(note);
                } while (cursor.moveToNext());
                if (listener != null){
                    listener.onSuccess(list);
                }
            } else if (listener != null){
                listener.onFailed("empty");
            }
        }
    }
}

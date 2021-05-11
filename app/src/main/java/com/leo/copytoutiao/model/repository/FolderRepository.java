package com.leo.copytoutiao.model.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leo.copytoutiao.model.bean.FolderBean;
import com.leo.copytoutiao.model.db.DataBaseHelper;
import com.taoke.base.BaseRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class FolderRepository extends BaseRepository<FolderBean> {
    private static volatile FolderRepository mInstance;
    private SQLiteDatabase database;
    private static final String TAG = "FolderRepository";

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
        insertFolder(folder.getUsername(), folder.getName());
    }


    /**
     * 单条文件夹同时写入本地和remote
     *
     * @param username
     * @param name
     */
    public void insertFolder(String username, String name) {
        //本地数据库库
        insertFolderToLocal(username, name);
        //远程数据库
        insertFolderToRemote(username, name);
    }

    /**
     * 单条文件夹写入本地
     *
     * @param username
     * @param name
     */
    public void insertFolderToLocal(String username, String name) {
        if (database != null) {
            database.execSQL("insert into folder values(?, ?)",
                    new String[]{username, name});
        }
    }

    /**
     * 单条文件夹写入remote
     *
     * @param username
     * @param name
     */
    public void insertFolderToRemote(String username, String name) {
        AVObject folder = new AVObject("Folder");
        // 为属性赋值
        folder.put("username", username);
        folder.put("name", name);
        // 将对象保存到云端
        folder.saveInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(AVObject todo) {
                // 成功保存之后，执行其他逻辑
                //System.out.println("保存成功。objectId：" + todo.getObjectId());
            }

            public void onError(Throwable throwable) {
                // 异常处理
            }

            public void onComplete() {
            }
        });
    }


    /**
     * 批量写入多条文件夹到本地和remote
     *
     * @param username
     * @param names
     */
    public void insertFolders(String username, String[] names) {
        //本地
        insertFoldersToLocal(username, names);
        //remote
        insertFoldersToRemote(username, names);
    }

    /**
     * 批量写入多条文件夹到本地
     *
     * @param username
     * @param names
     */
    public void insertFoldersToLocal(String username, String[] names) {
        for (String folderName : names) {
            insertFolderToLocal(username, folderName);
        }
    }

    public void insertFoldersToLocal(String username, List<FolderBean> folderBeans) {
        String[] names = new String[folderBeans.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = folderBeans.get(i).getName();
        }
        insertFoldersToLocal(username, names);
    }


    /**
     * 批量写入多条文件夹到remote
     *
     * @param username
     * @param names
     */
    public void insertFoldersToRemote(String username, String[] names) {
        for (String folderName : names) {
            insertFolderToRemote(username, folderName);
        }
    }

    /**
     * 删除某条分类，本地&remote
     *
     * @param username
     * @param name
     */
    public void deleteFolder(String username, String name) {
        deleteFolderFromLocal(username, name);
        deleteFolderFromRemote(username, name);
    }

    /**
     * 本地删除某条笔记
     *
     * @param username
     * @param name
     */
    public void deleteFolderFromLocal(String username, String name) {
        if (database != null) {
            database.execSQL("delete from folder where username = ? and name = ?",
                    new String[]{username, name});
        }
    }

    /**
     * remote删除某条笔记
     *
     * @param username
     * @param name
     */
    public void deleteFolderFromRemote(String username, String name) {
        AVQuery<AVObject> query = new AVQuery<>("Folder");
        query.whereEqualTo("username", username);
        query.whereEqualTo("name", name);
        query.getFirstInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(AVObject folder) {
                //真正执行Remote删除,踩坑需要subscrible才能删除成功
                folder.deleteInBackground().subscribe(new Observer<AVNull>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull AVNull avNull) {
                        Log.d(TAG,"remote删除文件夹成功");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG,"remote删除文件夹失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            public void onError(Throwable throwable) {
                Log.d(TAG,"remote查询需要删除的分类失败");
            }

            public void onComplete() {
            }
        });

    }

    /**
     * 查询用户所有文件夹子：local & remote
     *
     * @param username
     * @param listener
     */
    public void queryFolder(String username, LoadListener<FolderBean> listener) {
        if (database != null) {
            Cursor cursor = database.rawQuery("select * from folder where username = ?",
                    new String[]{username});
            if (cursor.moveToFirst()) {
                List<FolderBean> beanList = new ArrayList<>();
                do {
                    FolderBean bean = new FolderBean(cursor.getString(cursor.getColumnIndex("username")),
                            cursor.getString(cursor.getColumnIndex("name")));
                    beanList.add(bean);

                } while (cursor.moveToNext());
                if (listener != null) {
                    Log.d(TAG, "查询local文件夹：" + beanList.toString());
                    listener.onSuccess(beanList);
                }
            } else {
                //数据库没有,从远程查寻
                queryFolderFromRemote(username, listener);
            }
        }
    }

    /**
     * 从远程查询用户所有文件夹
     *
     * @param username
     * @param listener
     */
    public void queryFolderFromRemote(String username, LoadListener<FolderBean> listener) {
        AVQuery<AVObject> query = new AVQuery<>("Folder");
        query.whereEqualTo("username", username);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(List<AVObject> objects) {
                List<FolderBean> folderBeans = new ArrayList<>();
                for (AVObject avObject : objects) {
                    folderBeans.add(new FolderBean(avObject.getString("username"), avObject.getString("name")));
                }
                if (listener != null) {
                    Log.d(TAG, "remote文件夹：" + folderBeans.toString());
                    listener.onSuccess(folderBeans);
                    //写回本地数据库
                    insertFoldersToLocal(username, folderBeans);
                }

            }

            public void onError(Throwable throwable) {
                if (listener != null) {
                    listener.onFailed(throwable.getMessage());
                    Log.d(TAG, "remote查询用户文件夹失败：" + throwable.getMessage());
                }
            }

            public void onComplete() {
            }
        });
    }
}

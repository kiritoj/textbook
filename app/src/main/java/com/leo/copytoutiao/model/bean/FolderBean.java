package com.leo.copytoutiao.model.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FolderBean {
    private String username;
    private String name;

    public FolderBean(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof  FolderBean)){
            return false;
        }
        FolderBean bean = (FolderBean) obj;
        return username.equals(bean.username)  && name.equals(bean.name);
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + username + "," + name + "]";
    }
}

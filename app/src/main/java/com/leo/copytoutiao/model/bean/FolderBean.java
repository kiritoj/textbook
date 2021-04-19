package com.leo.copytoutiao.model.bean;

import androidx.annotation.Nullable;

public class FolderBean {
    private int userId;
    private String name;

    public FolderBean(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        return userId == bean.userId  && name.equals(bean.name);
    }
}

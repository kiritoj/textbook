package com.leo.copytoutiao.model.bean;

import java.io.Serializable;

/**
 * Created by tk on 2021/4/13
 */
public class UserBean implements Serializable {
    private final String name;
    private final String password;
    private final int userId;

    public UserBean(String name, String password) {
        this.name = name;
        this.password = password;
        this.userId = name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getUserId() {
        return userId;
    }
}

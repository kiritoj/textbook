package com.leo.copytoutiao.model.bean;

import java.io.Serializable;

/**
 * Created by tk on 2021/4/13
 */
public class UserBean implements Serializable {
    private final String name; //暂时只采用邮箱作用户名
    private final String password;

    public UserBean(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}

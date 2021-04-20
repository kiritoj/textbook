package com.leo.copytoutiao.model.bean;

import java.io.Serializable;

/**
 * Created by tk on 2021/4/12
 */
public class NoteBean implements Serializable {
    private String title;
    private String content;
    private String url;
    private String kind; //类别
    private long time;
    private final UserBean userBean;

    public NoteBean(String title, String content, String url, String kind, long time, UserBean userBean){
        this.title = title ;
        this.content = content;
        this.url = url;
        this.kind = kind;
        this.time = time;
        this.userBean = userBean;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserBean getUserBean() {
        return userBean;
    }
}

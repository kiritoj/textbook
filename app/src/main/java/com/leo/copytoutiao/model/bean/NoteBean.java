package com.leo.copytoutiao.model.bean;

import java.io.Serializable;

/**
 * Created by tk on 2021/4/12
 */
public class NoteBean implements Serializable {
    public String title = "无标题文档";
    public String content;
    public String url;
    public String kind; //类别
    public long time;

    public NoteBean(String title, String content, String url, String kind, long time){
        this.title = title;
        this.content = content;
        this.url = url;
        this.kind = kind;
        this.time = time;
    }
}

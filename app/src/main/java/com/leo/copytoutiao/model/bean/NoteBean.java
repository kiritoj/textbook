package com.leo.copytoutiao.model.bean;

/**
 * Created by tk on 2021/4/12
 */
public class NoteBean {
    public String title = "无标题文档";
    public String content;
    public String url;
    public long time;

    public NoteBean(String title, String content, String url, long time){
        this.title = title;
        this.content = content;
        this.url = url;
        this.time = time;
    }
}

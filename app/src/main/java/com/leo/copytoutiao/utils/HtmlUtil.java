package com.leo.copytoutiao.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tk on 2021/4/13
 * Html文本工具类
 */
public class HtmlUtil {
    //获取html文本第一张图片的src
    public static String getTextFromHtml(String html){
        String text = html.replaceAll("</?[^>]+>", " "); //剔出<html>的标签
        text = text.replaceAll("<a>\\s*|\t|\r|\n</a>", " ");//去除字符串中的空格,回车,换行符,制表符
        return text;
    }

    //获取html文本中图片url
    public static List<String> getUrlsFromHtml(String html){
        List<String> pics = new ArrayList<>();
        Pattern compile = Pattern.compile("<img.*?>", Pattern.CASE_INSENSITIVE);

        Matcher matcher = compile.matcher(html);
        while (matcher.find()) {
            String img = matcher.group();
            int index = img.indexOf("\"",10);
            img = img.substring(10,index);
            pics.add(img);
        }
        return pics;
    }

    public static String getFirstUrlFromHtml(String htlm){
        List<String> list = getUrlsFromHtml(htlm);
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}

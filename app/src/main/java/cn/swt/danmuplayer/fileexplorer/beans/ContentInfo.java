package cn.swt.danmuplayer.fileexplorer.beans;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Title: ContentInfo <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 18:35
 * Created by Wentao.Shi.
 */
public class ContentInfo extends RealmObject implements Comparable<ContentInfo>{
    @PrimaryKey
    private String contentPath;//目录路径
    private String contentName;//目录名
    // 非空字段
    private int count;

    public ContentInfo() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
        if (contentPath!=null&&contentPath.contains("/"))
            this.contentName=contentPath.split("/")[contentPath.split("/").length-1];
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }


    @Override
    public int compareTo(ContentInfo contentInfo) {
        return this.getContentName().compareTo(contentInfo.contentName);
    }
}

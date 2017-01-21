package cn.swt.danmuplayer.core.http.beans;

/**
 * Title: CidResponse <br>
 * Description: cid解析接口返回值 <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/12/3 0003 10:36
 * Created by Wentao.Shi.
 */
public class CidResponse {
    /**
     * title : 【10月】动画锻炼！EX 01_连载动画_番剧
     * pages : 1
     * cid : 4797865
     * partname :
     * parts : null
     */

    private String title;
    private int pages;
    private String cid;
    private String partname;
    private Object parts;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    public Object getParts() {
        return parts;
    }

    public void setParts(Object parts) {
        this.parts = parts;
    }
}

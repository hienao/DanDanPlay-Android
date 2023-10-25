package cn.swt.danmuplayer.fileexplorer.beans;

/**
 * Title: SearchResultInfo <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2017 <br>
 * Created DateTime: 2017-1-6 9:55
 * Created by Wentao.Shi.
 */
public class SearchResultInfo {
    private String mainTitle;
    private int type;
    private int id;
    private String title;
    private boolean isHeader;

    public SearchResultInfo() {

    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}

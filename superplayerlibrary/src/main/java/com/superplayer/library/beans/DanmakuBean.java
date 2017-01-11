package com.superplayer.library.beans;

/**
 * Title: DanmakuBean <br>
 * Description:用来存储的弹幕实体类 <br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-11 9:49
 * Created by Wentao.Shi.
 */
public class DanmakuBean {
    private String type;
    /**
     * 显示时间(毫秒)
     */
    private String time;
    /**
     * 文本
     */
    private String text;
    /**
     * 文本颜色
     */
    private String textColor;
    /**
     * 字体大小
     */
    private String textSize ;

    /**
     * 框的颜色,0表示无框
     */
    private int borderColor = 0;

    /**
     * 内边距(像素)
     */
    private int padding = 0;
    /**
     * 弹幕优先级,0为低优先级,>0为高优先级不会被过滤器过滤
     */
    private String priority ;

    /**
     * 索引/编号
     */
    private String index;
    /**
     * 弹幕发布者id, 0表示游客
     */
    private int userId = 0;
    /**
     * 发送时间的unix时间戳
     */
    private String sendtimeunix;

    /**
     * 弹幕发布者id
     */
    private String userHash;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSendtimeunix() {
        return sendtimeunix;
    }

    public void setSendtimeunix(String sendtimeunix) {
        this.sendtimeunix = sendtimeunix;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }
}

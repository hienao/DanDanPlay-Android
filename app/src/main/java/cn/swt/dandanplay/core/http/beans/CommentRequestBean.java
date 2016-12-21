package cn.swt.dandanplay.core.http.beans;

/**
 * Title: CommentRequestBean <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:51
 * Created by Wentao.Shi.
 */
public class CommentRequestBean {

    /**
     * Token : 1        用户登录令牌，设置为0即可
     * Time : 1.2       浮点数形式的弹幕时间，单位为秒
     * Mode : 3         弹幕模式，1普通弹幕，4底部弹幕，5顶部弹幕
     * Color : 4        32位整形数的弹幕颜色，算法为 R*256*256+G*256+B
     * Timestamp : 5    时间戳，设置为0即可
     * Pool : 6         弹幕池，设置为0即可
     * UId : 7          用户编号，匿名用户需设置为0
     * CId : 8          弹幕编号，设置为0即可
     * Message : sample 弹幕内容，文字长度不能超过200字。
     */

    private int Token;
    private double Time;
    private int Mode;
    private int Color;
    private int Timestamp;
    private int Pool;
    private int UId;
    private int CId;
    private String Message;

    public int getToken() {
        return Token;
    }

    public void setToken(int Token) {
        this.Token = Token;
    }

    public double getTime() {
        return Time;
    }

    public void setTime(double Time) {
        this.Time = Time;
    }

    public int getMode() {
        return Mode;
    }

    public void setMode(int Mode) {
        this.Mode = Mode;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int Color) {
        this.Color = Color;
    }

    public int getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(int Timestamp) {
        this.Timestamp = Timestamp;
    }

    public int getPool() {
        return Pool;
    }

    public void setPool(int Pool) {
        this.Pool = Pool;
    }

    public int getUId() {
        return UId;
    }

    public void setUId(int UId) {
        this.UId = UId;
    }

    public int getCId() {
        return CId;
    }

    public void setCId(int CId) {
        this.CId = CId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }
}

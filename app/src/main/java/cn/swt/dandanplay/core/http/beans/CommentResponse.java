package cn.swt.dandanplay.core.http.beans;

import java.util.List;

/**
 * Title: CommentResponse <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:30
 * Created by Wentao.Shi.
 */
public class CommentResponse {

    private List<CommentsBean> Comments;

    public List<CommentsBean> getComments() {
        return Comments;
    }

    public void setComments(List<CommentsBean> Comments) {
        this.Comments = Comments;
    }

    public static class CommentsBean {
        /**
         *Time: 浮点数形式的弹幕时间，单位为秒。
         * Mode: 弹幕模式，1普通弹幕，4底部弹幕，5顶部弹幕。
         * Color: 32位整形数的弹幕颜色，算法为 R*256*256 + G*256 + B。
         * Timestamp: 弹幕发送时间戳，单位为毫秒。可以理解为Unix时间戳，但起始点为1970年1月1日7:00:00。
         * Pool: 弹幕池，目前此数值为0。
         * UId: 用户编号，匿名用户为0，备份弹幕为-1，注册用户为正整数。
         * CId: 弹幕编号，此编号在同一个弹幕库中唯一，且新弹幕永远比旧弹幕编号要大。
         * Message: 弹幕内容文字。\r和\n不会作为换行转义符。
         * 以下为样例
         * Time : 1.1
         * Mode : 2
         * Color : 3
         * Timestamp : 4
         * Pool : 5
         * UId : 6
         * CId : 7
         * Message : sample string 8
         */

        private double Time;
        private int Mode;
        private int Color;
        private int Timestamp;
        private int Pool;
        private int UId;
        private int CId;
        private String Message;

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
}

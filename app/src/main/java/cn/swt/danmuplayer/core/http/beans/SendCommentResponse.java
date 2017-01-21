package cn.swt.danmuplayer.core.http.beans;

/**
 * Title: SendCommentResponse <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:36
 * Created by Wentao.Shi.
 */
public class SendCommentResponse {

    /**
     * Success : true       发送状态
     * CommentId : 2        弹幕ID
     * Error : sample string 3  失败信息
     */

    private boolean Success;
    private int CommentId;
    private String Error;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public int getCommentId() {
        return CommentId;
    }

    public void setCommentId(int CommentId) {
        this.CommentId = CommentId;
    }

    public String getError() {
        return Error;
    }

    public void setError(String Error) {
        this.Error = Error;
    }
}

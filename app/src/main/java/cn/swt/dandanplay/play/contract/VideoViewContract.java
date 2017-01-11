package cn.swt.dandanplay.play.contract;

import android.content.Context;

import cn.swt.dandanplay.core.base.BasePresenter;
import cn.swt.dandanplay.core.base.BaseView;
import cn.swt.dandanplay.core.http.beans.CommentResponse;

/**
 * Title: VideoViewContract <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/29 0029 9:42
 * Created by Wentao.Shi.
 */
public class VideoViewContract {
    public interface View extends BaseView {
        Context getContext();
        void gotComment(CommentResponse commentResponse);
        void setOtherCommentSourceNum(int num);
        void addOtherCommentSourceCount();
        void addBiliBiliDanmu(String a0,String a1,String a2,String a3,String a4,String a5,String a6,String a7,String text);
        String getVideoPath();
    }

    public interface Present extends BasePresenter {
        void getComment(String episodeId,String from);
        void getCommentSource(String episodeId);
        void getCommentOffline(String jsonstr,String xmlstr);
    }
}

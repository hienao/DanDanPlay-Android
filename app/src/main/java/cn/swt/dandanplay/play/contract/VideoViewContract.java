package cn.swt.dandanplay.play.contract;

import android.content.Context;

import cn.swt.dandanplay.core.base.BasePresenter;
import cn.swt.dandanplay.core.base.BaseView;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.core.http.beans.MatchResponse;

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
        void gotMatchEpisodeId(MatchResponse matchResponse);
        void gotComment(CommentResponse commentResponse);
        String getVideoFileHash(String filePath);
        long getVideoDuration(String path);
        void addBilibiliDanMu(String xmlUrl);
    }

    public interface Present extends BasePresenter {
        void matchEpisodeId(String filePath,String title,String hash,String length,String duration,String force);
        void getComment(String episodeId,String from);
        void getCommentSource(String episodeId);
    }
}

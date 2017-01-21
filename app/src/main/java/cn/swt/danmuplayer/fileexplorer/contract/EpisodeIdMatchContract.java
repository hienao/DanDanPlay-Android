package cn.swt.danmuplayer.fileexplorer.contract;

import java.util.List;

import cn.swt.danmuplayer.core.base.BasePresenter;
import cn.swt.danmuplayer.core.base.BaseView;
import cn.swt.danmuplayer.core.http.beans.MatchResponse;
import cn.swt.danmuplayer.fileexplorer.beans.SearchResultInfo;

/**
 * Title: EpisodeIdMatchContract <br>
 * Description:匹配视频EpisodeId的页面 <br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-5 16:19
 * Created by Wentao.Shi.
 */
public class EpisodeIdMatchContract {
    public interface View extends BaseView {
        void gotMatchEpisodeId(MatchResponse matchResponse);
        void gotSearchALLEpisodeId(List<SearchResultInfo> searchResultInfo);
        String getVideoFileHash(String filePath);
        long getVideoDuration(String path);
        void dismissProgressDialog();
    }

    public interface Present extends BasePresenter {
        void matchEpisodeId(String filePath,String title,String hash,String length,String duration,String force);
        void searchALLEpisodeId(String anime, String episodeId);
    }
}

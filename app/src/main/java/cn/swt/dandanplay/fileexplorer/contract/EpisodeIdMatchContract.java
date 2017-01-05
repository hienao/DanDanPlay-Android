package cn.swt.dandanplay.fileexplorer.contract;

import cn.swt.dandanplay.core.base.BasePresenter;
import cn.swt.dandanplay.core.base.BaseView;
import cn.swt.dandanplay.core.http.beans.MatchResponse;

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
        String getVideoFileHash(String filePath);
        long getVideoDuration(String path);
    }

    public interface Present extends BasePresenter {
        void matchEpisodeId(String filePath,String title,String hash,String length,String duration,String force);
    }
}

package cn.swt.dandanplay.play.presenter;

import com.swt.corelib.utils.LogUtils;

import java.util.List;

import javax.inject.Inject;

import cn.swt.dandanplay.core.http.APIService;
import cn.swt.dandanplay.core.http.HttpConstant;
import cn.swt.dandanplay.core.http.RetrofitManager;
import cn.swt.dandanplay.core.http.beans.CidResponse;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
import cn.swt.dandanplay.core.http.beans.RelatedResponse;
import cn.swt.dandanplay.play.contract.VideoViewContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Title: VideoViewPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/29 0029 9:44
 * Created by Wentao.Shi.
 */
public class VideoViewPresenter implements VideoViewContract.Present {
    private VideoViewContract.View mView;

    @Inject
    public VideoViewPresenter(VideoViewContract.View view) {
        mView = view;
    }

    @Override
    public void matchEpisodeId(String filePath, String title, String hash, String length, String duration, String force) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.matchEpisodeId(title, hash, length, duration, force), new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful()) {
                    MatchResponse matchResponse = response.body();
//                MatchResponse matchResponse= GsonManager.getInstance().fromJson(responseJson,MatchResponse.class);
                    mView.gotMatchEpisodeId(matchResponse);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "matchEpisodeId Error", t);
            }
        });
    }

    @Override
    public void getComment(String episodeId, String from) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getComment(episodeId, from), new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                mView.gotComment(commentResponse);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "commentResponse Error", t);
            }
        });
    }

    @Override
    public void getCommentSource(String episodeId) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getCommentSource(episodeId), new Callback<RelatedResponse>() {
            @Override
            public void onResponse(Call<RelatedResponse> call, Response<RelatedResponse> response) {
                RelatedResponse relatedResponse = response.body();
                getOtherComment(relatedResponse.getRelateds());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "commentResponse Error", t);
            }
        });

    }

    /**
     * 获取第三方弹幕源弹幕
     *
     * @param relatedsBeanList
     */
    private void getOtherComment(List<RelatedResponse.RelatedsBean> relatedsBeanList) {
        if (relatedsBeanList != null && relatedsBeanList.size() != 0) {
            for (RelatedResponse.RelatedsBean relatedsBean : relatedsBeanList) {
                if (relatedsBean.getProvider().contains("BiliBili")) {
                    //按bilibili解析弹幕
                    String biliVideoUrl = relatedsBean.getUrl();
                    String avnum = biliVideoUrl.substring(biliVideoUrl.lastIndexOf("/av") + 3, biliVideoUrl.lastIndexOf("/"));
                    String page = biliVideoUrl.substring(biliVideoUrl.lastIndexOf("_")+1,biliVideoUrl.lastIndexOf(".html"));
                    RetrofitManager retrofitManager = RetrofitManager.getInstance();
                    APIService apiService = retrofitManager.create(HttpConstant.BiliBili_CID_GET);
                    retrofitManager.enqueue(apiService.getBiliBiliCid(avnum,page), new Callback<CidResponse>() {
                        @Override
                        public void onResponse(Call<CidResponse> call, Response<CidResponse> response) {
                            CidResponse cidResponse = response.body();
                            //获取xml弹幕地址
                            String xmlurl="https://comment.bilibili.com/"+cidResponse.getCid()+".xml";
                            mView.addBilibiliDanMu(xmlurl);
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            LogUtils.e("VideoViewPresenter", "CidResponse Error", t);
                        }
                    });
                } else if (relatedsBean.getProvider().contains("Acfun")) {

                } else if (relatedsBean.getProvider().contains("Tucao")) {

                }
            }
        }

    }
}

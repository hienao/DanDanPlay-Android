package cn.swt.dandanplay.fileexplorer.presenter;

import com.swt.corelib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.swt.dandanplay.core.http.APIService;
import cn.swt.dandanplay.core.http.RetrofitManager;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
import cn.swt.dandanplay.core.http.beans.SearchAllResponse;
import cn.swt.dandanplay.fileexplorer.beans.SearchResultInfo;
import cn.swt.dandanplay.fileexplorer.contract.EpisodeIdMatchContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Title: EpisodeIdMatchPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-5 16:20
 * Created by Wentao.Shi.
 */
public class EpisodeIdMatchPresenter implements EpisodeIdMatchContract.Present {
    private EpisodeIdMatchContract.View mView;
    @Inject
    EpisodeIdMatchPresenter(EpisodeIdMatchContract.View view){
        mView=view;
    }

    /**
     * 使用文件匹配视频信息
     * @param filePath
     * @param title
     * @param hash
     * @param length
     * @param duration
     * @param force
     */
    @Override
    public void matchEpisodeId(String filePath, String title, String hash, String length, String duration, String force) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.matchEpisodeId(title, hash, length, duration, force), new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {
                if (response.isSuccessful()) {
                    MatchResponse matchResponse = response.body();
                    mView.gotMatchEpisodeId(matchResponse);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "matchEpisodeId Error", t);
            }
        });
    }

    /**
     * 搜索视频信息
     * @param anime
     * @param episodeId
     */
    @Override
    public void searchALLEpisodeId(String anime, String episodeId) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.searchALLEpisodeId(anime,episodeId), new Callback<SearchAllResponse>() {
            @Override
            public void onResponse(Call<SearchAllResponse> call, Response<SearchAllResponse> response) {
                if (response.isSuccessful()) {
                    SearchAllResponse searchAllResponse = response.body();
                    List<SearchAllResponse.AnimesBean>contentlist=searchAllResponse.getAnimes();
                    if (contentlist!=null&&contentlist.size()!=0){
                        List<SearchResultInfo>searchResultInfoList=new ArrayList<SearchResultInfo>();
                        for (int i=0;i<contentlist.size();i++){
                            SearchAllResponse.AnimesBean animesBean=contentlist.get(i);
                            String mainTitle=animesBean.getTitle();
                            int type=animesBean.getType();
                            SearchResultInfo searchResultInfoHeader=new SearchResultInfo();
                            searchResultInfoHeader.setMainTitle(mainTitle);
                            searchResultInfoHeader.setType(type);
                            searchResultInfoHeader.setHeader(true);
                            searchResultInfoList.add(searchResultInfoHeader);
                            List<SearchAllResponse.AnimesBean.EpisodesBean>episodesBeanList=animesBean.getEpisodes();
                            if (episodesBeanList!=null&&episodesBeanList.size()!=0){
                                for (SearchAllResponse.AnimesBean.EpisodesBean episodesBean:episodesBeanList){
                                    SearchResultInfo searchResultInfo=new SearchResultInfo();
                                    searchResultInfo.setTitle(episodesBean.getTitle());
                                    searchResultInfo.setId(episodesBean.getId());
                                    searchResultInfo.setMainTitle(mainTitle);
                                    searchResultInfo.setType(type);
                                    searchResultInfo.setHeader(false);
                                    searchResultInfoList.add(searchResultInfo);
                                }
                            }
                        }
                        mView.gotSearchALLEpisodeId(searchResultInfoList);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "searchEpisodeId Error", t);
            }
        });
    }
}

package cn.swt.dandanplay.fileexplorer.presenter;

import com.swt.corelib.utils.LogUtils;

import javax.inject.Inject;

import cn.swt.dandanplay.core.http.APIService;
import cn.swt.dandanplay.core.http.RetrofitManager;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
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
}

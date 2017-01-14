package cn.swt.dandanplay.play.presenter;

import javax.inject.Inject;

import cn.swt.dandanplay.play.contract.VideoViewContract;

/**
 * Title: VideoViewPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/29 0029 9:44
 * Created by Wentao.Shi.
 */
public class VideoViewPresenter implements VideoViewContract.Present {
    private VideoViewContract.View mView;
    private String videoPath;

    @Inject
    public VideoViewPresenter(VideoViewContract.View view) {
        mView = view;
    }




}

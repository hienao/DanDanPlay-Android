package cn.swt.dandanplay.play.module;

import cn.swt.dandanplay.play.contract.VideoViewContract;
import dagger.Module;
import dagger.Provides;

/**
 * Title: MainModule <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/17 11:01
 * Created by Wentao.Shi.
 */
@Module
public class MainModule {
    private VideoViewContract.View mVideoView;
    public MainModule(VideoViewContract.View view){
        mVideoView=view;
    }
    @Provides
    VideoViewContract.View provideVideoView(){
        return mVideoView;
    }
}

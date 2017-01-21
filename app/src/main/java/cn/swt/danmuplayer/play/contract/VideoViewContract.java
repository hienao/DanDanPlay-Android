package cn.swt.danmuplayer.play.contract;

import android.content.Context;

import cn.swt.danmuplayer.core.base.BasePresenter;
import cn.swt.danmuplayer.core.base.BaseView;

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
        String getVideoPath();
    }

    public interface Present extends BasePresenter {
    }
}

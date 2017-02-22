package cn.swt.danmuplayer.fileexplorer.beans;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Title: VideoFileArgInfo <br>
 * Description: 记录视频是否有弹幕及观看进度等参数的bean<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-2-21 9:23
 * Created by Wentao.Shi.
 */
public class VideoFileArgInfo extends RealmObject {
    /**
     * 视频文件路径
     */
    @PrimaryKey
    String videoPath;
    /**
     * 是否有本地弹幕
     */
    boolean haveLocalDanmu;
    int sawProgress;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean isHaveLocalDanmu() {
        return haveLocalDanmu;
    }

    public void setHaveLocalDanmu(boolean haveLocalDanmu) {
        this.haveLocalDanmu = haveLocalDanmu;
    }

    public int getSawProgress() {
        return sawProgress;
    }

    public void setSawProgress(int sawProgress) {
        this.sawProgress = sawProgress;
    }
}

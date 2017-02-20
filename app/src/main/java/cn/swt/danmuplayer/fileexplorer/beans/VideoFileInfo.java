package cn.swt.danmuplayer.fileexplorer.beans;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Title: VideoFileInfo <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 18:37
 * Created by Wentao.Shi.
 */
public class VideoFileInfo extends RealmObject implements Comparable<VideoFileInfo>{
    /**
     * 视频文件路径
     */
    @PrimaryKey
    String videoPath;
    /**
     * 视频名称(不含后缀)
     */
    String videoNameWithoutSuffix;
    /**
     * 视频名称(含后缀)
     */
    String videoName;
    /**
     * 视频所在目录Path
     */
    String videoContentPath;
    /**
     * 视频封面bitmap的base64 字符串
     */
    String cover;
    /**
     * 视频时长
     */
    String videoLength;
    /**
     * 是否有本地弹幕
     */
    boolean haveLocalDanmu;

    public VideoFileInfo() {
    }

    public VideoFileInfo(String videoPath) {
        this.videoPath = videoPath;
    }

    public VideoFileInfo(String videoNameWithoutSuffix, String videoName, String videoPath, String cover, String videoLength) {
        this.videoNameWithoutSuffix = videoNameWithoutSuffix;
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.cover = cover;
        this.videoLength = videoLength;
    }

    public String getVideoNameWithoutSuffix() {
        return videoNameWithoutSuffix;
    }

    public void setVideoNameWithoutSuffix(String videoNameWithoutSuffix) {
        this.videoNameWithoutSuffix = videoNameWithoutSuffix;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoContentPath() {
        return videoContentPath;
    }

    public void setVideoContentPath(String videoContentPath) {
        this.videoContentPath = videoContentPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }


    public boolean isHaveLocalDanmu() {
        return haveLocalDanmu;
    }

    public void setHaveLocalDanmu(boolean haveLocalDanmu) {
        this.haveLocalDanmu = haveLocalDanmu;
    }

    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "videoNameWithoutSuffix='" + videoNameWithoutSuffix + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoContentPath='" + videoContentPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", cover=" + cover +
                ", videoLength='" + videoLength + '\'' +
                '}';
    }

    @Override
    public int compareTo(VideoFileInfo videoFileInfo) {
        return this.getVideoName().compareTo(videoFileInfo.getVideoName());
    }
}

package cn.swt.dandanplay.fileexplorer.utils;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.swt.dandanplay.fileexplorer.beans.DanmakuBean;
import cn.swt.dandanplay.play.view.VideoViewActivity;

/**
 * Title: DanmuUtils <br>
 * Description: 弹幕存储器<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-13 13:59
 * Created by Wentao.Shi.
 */
public class DanmuUtils {
    private static Context mContext;
    private static DanmuUtils instance;
    private static List<DanmakuBean> mDanmakuBeanList;
    private static String videoPath, fileTitle, title;
    private static int episode_id;

    private DanmuUtils() {
    }

    public static synchronized DanmuUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DanmuUtils();
            mDanmakuBeanList = new ArrayList<>();
        }
        mContext = context;
        return instance;
    }

    public void clearDanmulist() {
        mDanmakuBeanList.clear();
    }

    public void addDanmu(DanmakuBean danmakuBean) {
        mDanmakuBeanList.add(danmakuBean);
    }

    public void addDanmuList(List<DanmakuBean> danmakuBeanList) {
        if (danmakuBeanList != null && danmakuBeanList.size() != 0) {
            mDanmakuBeanList.addAll(danmakuBeanList);
        }
    }

    /**
     * 将弹幕列表输出到指定的xml文件
     *
     * @param xmlpath xml文件路径
     */
    public void exportDanmuList2Xml(String xmlpath) {

    }

    /**
     * 从dandanpaly识别到的视频id获取所有网站弹幕信息
     *
     * @param espoisedId
     */
    public void getDanmuListByEspoisedId(int espoisedId) {
        danmuGetFinish();
    }

    public Context getmContext() {
        return mContext;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public DanmuUtils setVideoPath(String videoPath) {
        DanmuUtils.videoPath = videoPath;
        return instance;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public DanmuUtils setFileTitle(String fileTitle) {
        DanmuUtils.fileTitle = fileTitle;
        return instance;
    }

    public String getTitle() {
        return title;
    }

    public DanmuUtils setTitle(String title) {
        DanmuUtils.title = title;
        return instance;
    }

    public int getEpisode_id() {
        return episode_id;
    }

    public DanmuUtils setEpisode_id(int episode_id) {
        DanmuUtils.episode_id = episode_id;
        return instance;
    }

    /**
     * 弹幕获取完成之后的操作，一般为跳转
     */
    public static void danmuGetFinish() {
        if (mContext != null) {
            mContext.startActivity(new Intent(mContext, VideoViewActivity.class)
                    .putExtra("path", videoPath)
                    .putExtra("file_title", fileTitle)
                    .putExtra("title", title).putExtra("episode_id", episode_id));
        }
    }
}

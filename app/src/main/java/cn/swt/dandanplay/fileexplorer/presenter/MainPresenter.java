package cn.swt.dandanplay.fileexplorer.presenter;


import android.media.MediaPlayer;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.SDCardUtils;
import com.swt.corelib.utils.TimeUtils;
import com.swt.corelib.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import cn.swt.dandanplay.application.MyApplication;
import cn.swt.dandanplay.fileexplorer.beans.ContentInfo;
import cn.swt.dandanplay.fileexplorer.beans.VideoFileInfo;
import cn.swt.dandanplay.fileexplorer.contract.MainContract;
import cn.swt.dandanplay.others.VideoFileNameFilter;

/**
 * Title: MainPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/17 10:55
 * Created by Wentao.Shi.
 */
public class MainPresenter implements MainContract.Present {

    private MainContract.View mView;

    @Inject
    MainPresenter(MainContract.View view) {
        mView = view;
    }

    /**
     * 获取外部存储文件列表
     */
    @Override
    public void getFileContentData() {
        if (SDCardUtils.isSDCardEnable()){
            MyApplication.getLiteOrm().deleteAll(ContentInfo.class);
            MyApplication.getLiteOrm().deleteAll(VideoFileInfo.class);
            String SDcardPath=SDCardUtils.getSDCardPath();
            List<File> filelist=FileUtils.listFilesInDirWithFilter(SDcardPath, new VideoFileNameFilter(true),true);
            Map<String,Integer>contentInfoIntegerMap=new TreeMap<>();
            List<VideoFileInfo>videoFileInfoList=new ArrayList<>();
            List<ContentInfo>contentInfoList=new ArrayList<>();
            if (filelist!=null){
                for (File f:filelist){
                    //目录判断
                    if (contentInfoIntegerMap.containsKey(FileUtils.getDirName(f))){
                        contentInfoIntegerMap.put(FileUtils.getDirName(f),contentInfoIntegerMap.get(FileUtils.getDirName(f))+1);
                    }else {
                        contentInfoIntegerMap.put(FileUtils.getDirName(f),1);
                    }
                    //文件判断
                    VideoFileInfo videoFileInfo=new VideoFileInfo();
                    videoFileInfo.setVideoPath(f.getPath());
                    videoFileInfo.setVideoName(f.getName());
                    videoFileInfo.setVideoNameWithoutSuffix(f.getName().substring(0,f.getName().lastIndexOf(".")));
                    videoFileInfo.setCover(null);
//                    IMediaPlayer mMediaPlayer = new IjkVideoView(mView.getContext()).createPlayer(new Settings(mView.getContext()).getPlayer());

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
//                        IMediaDataSource dataSource = new FileMediaDataSource(f);
//                        mMediaPlayer.setDataSource(dataSource);
//                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(mMediaPlayer.getDuration()));

                        mediaPlayer.setDataSource(new FileInputStream(f).getFD());
                        mediaPlayer.prepare();
                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(mediaPlayer.getDuration()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
//                        mMediaPlayer.release();
                        mediaPlayer.release();
                    }
                    videoFileInfoList.add(videoFileInfo);
                }
            }
            for (String key:contentInfoIntegerMap.keySet()){
                contentInfoList.add(new ContentInfo(key,contentInfoIntegerMap.get(key)));
            }
            MyApplication.getLiteOrm().save(contentInfoList);
            MyApplication.getLiteOrm().save(videoFileInfoList);
            mView.getDataFromSQLite();
        }else {
            ToastUtils.showShortToastSafe(mView.getContext(),"外部存储不可用");
        }

    }
}

package cn.swt.danmuplayer.fileexplorer.presenter;

import android.media.MediaPlayer;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.SDCardUtils;
import com.swt.corelib.utils.TimeUtils;
import com.swt.corelib.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.contract.FileExplorerContract;
import cn.swt.danmuplayer.others.VideoFileNameFilter;

/**
 * Title: FileExplorerPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/23 0023 9:30
 * Created by Wentao.Shi.
 */
public class FileExplorerPresenter implements FileExplorerContract.Present {
    private FileExplorerContract.View mView;
    @Inject
    FileExplorerPresenter(FileExplorerContract.View view){
        mView=view;
    }

    @Override
    public void getFileData(String contentpath) {
        if (SDCardUtils.isSDCardEnable()){
            List<File> filelist= FileUtils.listFilesInDirWithFilter(contentpath, new VideoFileNameFilter(true),true);
            List<VideoFileInfo>videoFileInfoList=new ArrayList<>();
            if (filelist!=null){
                for (File f:filelist){
                    VideoFileInfo videoFileInfo=new VideoFileInfo();
                    videoFileInfo.setVideoPath(f.getPath());
                    videoFileInfo.setVideoName(f.getName());
                    videoFileInfo.setVideoNameWithoutSuffix(f.getName().substring(0,f.getName().lastIndexOf(".")));
                    LogUtils.i("视频路径:"+f.getPath());
                    videoFileInfo.setCover(null);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(f.getPath());
                        mediaPlayer.prepare();
                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(mediaPlayer.getDuration()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        mediaPlayer.release();
                    }
                    videoFileInfoList.add(videoFileInfo);
                }
            }
            mView.setData(videoFileInfoList);

        }else {
            ToastUtils.showShortToastSafe(mView.getContext(),"外部存储不可用");
        }
    }
}

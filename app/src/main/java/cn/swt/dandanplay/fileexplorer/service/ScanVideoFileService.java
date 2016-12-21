package cn.swt.dandanplay.fileexplorer.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.IBinder;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.SDCardUtils;
import com.swt.corelib.utils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.swt.dandanplay.application.MyApplication;
import cn.swt.dandanplay.fileexplorer.beans.ContentInfo;
import cn.swt.dandanplay.fileexplorer.beans.VideoFileInfo;
import cn.swt.dandanplay.others.VideoFileNameFilter;

public class ScanVideoFileService extends IntentService {
    public ScanVideoFileService() {
        super("");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SDCardUtils.isSDCardEnable()) {
            MyApplication.getLiteOrm().deleteAll(ContentInfo.class);
            MyApplication.getLiteOrm().deleteAll(VideoFileInfo.class);
            String SDcardPath = SDCardUtils.getSDCardPath();
            List<File> filelist = FileUtils.listFilesInDirWithFilter(SDcardPath, new VideoFileNameFilter(true), true);
            Map<String, Integer> contentInfoIntegerMap = new TreeMap<>();
            List<VideoFileInfo> videoFileInfoList = new ArrayList<>();
            List<ContentInfo> contentInfoList = new ArrayList<>();
            if (filelist != null) {
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                for (File f : filelist) {
                    //目录判断
                    if (contentInfoIntegerMap.containsKey(FileUtils.getDirName(f))) {
                        contentInfoIntegerMap.put(FileUtils.getDirName(f), contentInfoIntegerMap.get(FileUtils.getDirName(f)) + 1);
                    } else {
                        contentInfoIntegerMap.put(FileUtils.getDirName(f), 1);
                    }
                    //文件判断
                    VideoFileInfo videoFileInfo = new VideoFileInfo();
                    videoFileInfo.setVideoPath(f.getPath());
                    videoFileInfo.setVideoContentPath(f.getPath().substring(0,f.getPath().lastIndexOf("/")+1));
                    videoFileInfo.setVideoName(f.getName());
                    videoFileInfo.setVideoNameWithoutSuffix(f.getName().substring(0, f.getName().lastIndexOf(".")));
                    videoFileInfo.setCover(null);
                    try {
                        retr.setDataSource(f.getPath());
                        videoFileInfo.setCover(getVideoThumbnail(f.getPath(),200,100,ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(Long.parseLong(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.i("ERROR:---------"+e.toString());
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(new FileInputStream(f).getFD());
                        mediaPlayer.prepare();
                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(mediaPlayer.getDuration()));
                    } catch (IOException e) {
                        LogUtils.i(e.toString());
                        e.printStackTrace();
                    }
                    finally {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                    }
                    videoFileInfoList.add(videoFileInfo);
                }
            }

            for (String key : contentInfoIntegerMap.keySet()) {
                contentInfoList.add(new ContentInfo(key, contentInfoIntegerMap.get(key)));
            }
            MyApplication.getLiteOrm().insert(contentInfoList);
            MyApplication.getLiteOrm().insert(videoFileInfoList);
        }
        //发送广播
        Intent myintent=new Intent();
        myintent.putExtra("scanfinish",true);
        myintent.setAction("cn.swt.dandanplay.ScanVideoFileService");
        sendBroadcast(myintent);

    }
    /**
     * 获取视频缩略图
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}

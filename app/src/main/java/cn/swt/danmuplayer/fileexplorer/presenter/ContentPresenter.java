package cn.swt.danmuplayer.fileexplorer.presenter;


import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.swt.corelib.beans.StorageInfo;
import com.swt.corelib.utils.ConvertUtils;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.ImageUtils;
import com.swt.corelib.utils.StorageUtils;
import com.swt.corelib.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.fileexplorer.beans.ContentInfo;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileArgInfo;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.contract.MainContract;
import io.realm.Realm;
import io.realm.RealmResults;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Title: ContentPresenter <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2016 <br>
 * Created DateTime: 2016/10/17 10:55
 * Created by Wentao.Shi.
 */
public class ContentPresenter implements MainContract.Present {
    private static final int MSG_SCAN_FINISH = 0;
    private MainContract.View mView;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SCAN_FINISH:
                    mView.getDataFromSQLite();
                    break;
                default:
                    break;
            }
        }

    };

    public ContentPresenter(MainContract.View view) {
        mView = view;
    }


    @Override
    public void getAllVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] suffixList = {".asf", ".avi", ".mkv", ".mp4", ".wmv", ".3gp", ".flv"};
                List<StorageInfo> storageInfoList = StorageUtils.listAvaliableStorage(mView.getContext());
                List<File> fileList = new ArrayList<File>();
                if (storageInfoList == null || storageInfoList.size() == 0)
                    return;
                for (StorageInfo s : storageInfoList) {
                    //获取每一个挂载点的文件
                    for (String ss : suffixList) {
                        fileList.addAll(FileUtils.listFilesInDirWithFilter(s.path, ss, true));
                    }
                }
                Realm realm = MyApplication.getRealmInstance();
                if (fileList == null || fileList.size() == 0)
                    return;
                for (File f : fileList) {
                    String filePath = f.getPath();
                    FFmpegMediaMetadataRetriever fmmr = null;
                    Bitmap bitmap = null;
                    String videoDuration = null;
                    try {
                        fmmr = new FFmpegMediaMetadataRetriever();
                        fmmr.setDataSource(filePath);
                        bitmap = fmmr.getFrameAtTime();
                        videoDuration = fmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    String videoName = f.getName();
                    fmmr.release();
                    //保存弹幕及观看进度信息到数据库
                    VideoFileArgInfo videoFileArgInfo = realm.where(VideoFileArgInfo.class).equalTo("videoPath", filePath).findFirst();
                    String ddxml = null;
                    String bilixml = null;
                    ddxml = filePath.substring(0, filePath.lastIndexOf(".")) + "dd.xml";
                    bilixml = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";
                    realm.beginTransaction();
                    if (videoFileArgInfo == null) {
                        videoFileArgInfo = new VideoFileArgInfo();
                        videoFileArgInfo.setVideoPath(filePath);
                        videoFileArgInfo.setSawProgress(0);
                        if (FileUtils.isFileExists(ddxml) || FileUtils.isFileExists(bilixml)) {
                            videoFileArgInfo.setHaveLocalDanmu(true);
                        } else {
                            videoFileArgInfo.setHaveLocalDanmu(false);
                        }
                        realm.copyToRealm(videoFileArgInfo);
                    } else {
                        if (FileUtils.isFileExists(ddxml) || FileUtils.isFileExists(bilixml)) {
                            videoFileArgInfo.setHaveLocalDanmu(true);
                        } else {
                            videoFileArgInfo.setHaveLocalDanmu(false);
                        }

                    }
                    realm.commitTransaction();
                    VideoFileInfo videoFileInfoc = realm.where(VideoFileInfo.class).equalTo("videoPath", filePath).findFirst();
                    //文件已存在，跳过
                    if (videoFileInfoc != null)
                        continue;
                    //文件不存在，添加
                    VideoFileInfo videoFileInfo = new VideoFileInfo();
                    videoFileInfo.setVideoPath(filePath);
                    videoFileInfo.setVideoContentPath(filePath.substring(0, filePath.lastIndexOf("/") + 1));
                    if (TextUtils.isEmpty(videoName)) {
                        videoFileInfo.setVideoName(videoName);
                        videoFileInfo.setVideoNameWithoutSuffix("Unkonwn");
                    } else if (videoName.contains(".")) {
                        videoFileInfo.setVideoName(videoName);
                        videoFileInfo.setVideoNameWithoutSuffix(videoName.substring(0, videoName.lastIndexOf(".")));
                    } else {
                        videoFileInfo.setVideoName(videoName);
                        videoFileInfo.setVideoNameWithoutSuffix(videoName);
                    }
                    videoFileInfo.setCover(ConvertUtils.bitmapToBase64(bitmap));
                    try {
                        videoFileInfo.setVideoLength(TimeUtils.formatDuring(Long.parseLong(videoDuration)));
                    } catch (NumberFormatException e) {
                        videoFileInfo.setVideoLength("UnKnown");
                    }
                    realm.beginTransaction();
                    realm.copyToRealm(videoFileInfo);
                    realm.commitTransaction();
                }
                //查询数据库中是否有被删除的视频，有的话删除记录
                RealmResults<VideoFileInfo> videolist = realm.where(VideoFileInfo.class).findAll();
                if (videolist != null && videolist.size() != 0) {
                    for (final VideoFileInfo v : videolist) {
                        //文件不存在
                        if (!FileUtils.isFileExists(v.getVideoPath())) {
                            realm.beginTransaction();
                            v.deleteFromRealm();
                            realm.commitTransaction();
                        }
                    }
                }
                restoreContentTable();
                mHandler.sendEmptyMessage(MSG_SCAN_FINISH);
                Message msg = new Message();
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 更新目录信息数据库
     */
    public static void restoreContentTable() {
        Realm realm = MyApplication.getRealmInstance();
        //重建目录信息
        RealmResults<VideoFileInfo> videoFileInfos = realm.where(VideoFileInfo.class).findAll();
        List<VideoFileInfo> videolist = realm.copyFromRealm(videoFileInfos);
        List<String> pathlist = new ArrayList<>();
        if (videolist != null && videolist.size() > 0) {
            for (VideoFileInfo v : videolist) {
                if (pathlist.contains(v.getVideoContentPath()))
                    continue;
                pathlist.add(v.getVideoContentPath());
            }
        }
        //删除为空的目录
        RealmResults<ContentInfo> contentInfos = realm.where(ContentInfo.class).findAll();
        if (contentInfos != null && contentInfos.size() > 0) {
            for (ContentInfo c : contentInfos) {
                if (!pathlist.contains(c.getContentPath())) {
                    realm.beginTransaction();
                    c.deleteFromRealm();
                    realm.commitTransaction();
                }
            }
        }
        if (pathlist != null && pathlist.size() != 0) {
            for (int i = 0; i < pathlist.size(); i++) {
                videoFileInfos = realm.where(VideoFileInfo.class).equalTo("videoContentPath", pathlist.get(i)).findAll();
                ContentInfo contentInfo = realm.where(ContentInfo.class).equalTo("contentPath", pathlist.get(i)).findFirst();
                if (contentInfo == null) {
                    contentInfo = new ContentInfo();
                    contentInfo.setContentPath(pathlist.get(i));
                    contentInfo.setCount(videoFileInfos.size());
                    realm.beginTransaction();
                    realm.copyToRealm(contentInfo);
                    realm.commitTransaction();
                } else {
                    realm.beginTransaction();
                    contentInfo.setCount(videoFileInfos.size());
                    realm.commitTransaction();
                }
            }
        }
    }

    /**
     * 获取的图片是base64 code
     *
     * @param filePath
     * @return
     */
    public static String getVideoThumbnail(String filePath) {
        String result = "";
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
            bitmap = ImageUtils.scale(bitmap, 320, 180);
            result = ConvertUtils.bitmapToBase64(bitmap);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

package cn.swt.dandanplay.fileexplorer.presenter;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.swt.corelib.utils.ConvertUtils;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.ImageUtils;
import com.swt.corelib.utils.SDCardUtils;
import com.swt.corelib.utils.TimeUtils;
import com.swt.corelib.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import cn.swt.dandanplay.application.MyApplication;
import cn.swt.dandanplay.fileexplorer.beans.ContentInfo;
import cn.swt.dandanplay.fileexplorer.beans.VideoFileInfo;
import cn.swt.dandanplay.fileexplorer.contract.MainContract;

/**
 * Title: MainPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/17 10:55
 * Created by Wentao.Shi.
 */
public class MainPresenter implements MainContract.Present {

    private static final int MSG_SCAN_FINISH=0;
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
    @Inject
    MainPresenter(MainContract.View view) {
        mView = view;
    }



    @Override
    public void getAllVideo(final ContentResolver contentResolver) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                if (SDCardUtils.isSDCardEnable()) {
                    MyApplication.getLiteOrm().deleteAll(ContentInfo.class);
                    MyApplication.getLiteOrm().deleteAll(VideoFileInfo.class);
                    String SDcardPath = SDCardUtils.getSDCardPath();
                    List<VideoFileInfo> videos = new ArrayList<>();
                    List<ContentInfo>contentInfoList=new ArrayList<>();
                    Map<String,Integer> contentInfoIntegerMap=new TreeMap<>();
                    String[] projection = new String[]{ MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DURATION};

                    //相当于我们常用sql where 后面的写法
//            String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
//                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
//                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
//                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
//                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";
//
//            String[] selectionArgs = new String[]{"text/plain", "application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel"};

                    Cursor cursor = contentResolver.query(MediaStore.Video.Media.getContentUri("external"), projection, /*selection*/null, /*selectionArgs*/null,  MediaStore.Video.Media.DISPLAY_NAME + " desc");

                    String videoId;

                    String videoName;

                    String filePath;
                    String fileSize;
                    String videoDuration;
                    while (cursor.moveToNext()) {
                        videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        fileSize = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        videoDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        File f=new File(filePath);
                        //目录判断
                        if (contentInfoIntegerMap.containsKey(FileUtils.getDirName(f))){
                            contentInfoIntegerMap.put(FileUtils.getDirName(f),contentInfoIntegerMap.get(FileUtils.getDirName(f))+1);
                        }else {
                            contentInfoIntegerMap.put(FileUtils.getDirName(f),1);
                        }
                        Log.e("ryze_text", videoId + " -- " + videoName + " -- " + "--" + fileSize + filePath);
                        //文件判断
                        VideoFileInfo videoFileInfo = new VideoFileInfo();
                        videoFileInfo.setVideoPath(filePath);
                        videoFileInfo.setVideoContentPath(filePath.substring(0,filePath.lastIndexOf("/")+1));
                        videoFileInfo.setVideoName(videoName);
                        videoFileInfo.setVideoNameWithoutSuffix(videoName.substring(0,videoName.lastIndexOf(".")));
                        videoFileInfo.setCover(getVideoThumbnail(filePath));
                        try {
                            videoFileInfo.setVideoLength(TimeUtils.formatDuring(Long.parseLong(videoDuration)));
                        } catch (NumberFormatException e) {
                            videoFileInfo.setVideoLength("UnKnown");
                        }
                        videos.add(videoFileInfo);
                    }
                    for (String key:contentInfoIntegerMap.keySet()){
                        contentInfoList.add(new ContentInfo(key,contentInfoIntegerMap.get(key)));
                    }
                    MyApplication.getLiteOrm().save(contentInfoList);
                    MyApplication.getLiteOrm().save(videos);
                    cursor.close();
                    cursor = null;
                    mHandler.sendEmptyMessage(MSG_SCAN_FINISH);
                    Message msg =new Message();
                    mHandler.sendMessage(msg);
                }else {
                    ToastUtils.showShortToastSafe(mView.getContext(),"外部存储不可用");
                }
            }

        }).start();

    }

    /**
     * 获取的图片是base64 code
     * @param filePath
     * @return
     */
    public String getVideoThumbnail(String filePath) {
        String result="";
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
            bitmap = ImageUtils.scale(bitmap,160,90);
            result= ConvertUtils.bitmapToBase64(bitmap);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

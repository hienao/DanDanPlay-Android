package cn.swt.danmuplayer.play.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.dl7.player.media.IjkPlayerView;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.swt.danmuplayer.play.contract.VideoViewContract;
import cn.swt.danmuplayer.play.presenter.VideoViewPresenter;

public class VideoViewActivity extends AppCompatActivity implements VideoViewContract.View {
    VideoViewPresenter mVideoViewPresenter;
    com.dl7.player.media.IjkPlayerView mViewSuperPlayer;
    private String videoPath, videoTitle, file_title;
    private boolean hide_danmu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewSuperPlayer = new IjkPlayerView(this);
        setContentView(mViewSuperPlayer);
        initData();
        initPlayer();
        initView();

    }


    @Override
    public String getVideoPath() {
        return videoPath;
    }


    private void initData() {
        mVideoViewPresenter = new VideoViewPresenter(this);
        videoPath = getIntent().getStringExtra("path");
        file_title = getIntent().getStringExtra("file_title");
        videoTitle = getIntent().getStringExtra("title");
        hide_danmu = getIntent().getBooleanExtra("hide_danmu", false);
    }

    private void initView() {
        if (!TextUtils.isEmpty(videoTitle)) {
            mViewSuperPlayer.setTitle(videoTitle);
        } else {
            mViewSuperPlayer.setTitle(file_title);
        }
    }

    private void initPlayer() {

        String danmuxml = "";
        InputStream inputStream=null;
        if (!TextUtils.isEmpty(videoPath)) {
            String ddxml = videoPath.substring(0, videoPath.lastIndexOf(".")) + "dd.xml";
            String bilixml = videoPath.substring(0, videoPath.lastIndexOf(".")) + ".xml";
            if (FileUtils.isFileExists(ddxml)) {
                danmuxml = ddxml;
            } else if (FileUtils.isFileExists(bilixml)) {
                danmuxml = bilixml;
            }
        }

        if (!TextUtils.isEmpty(danmuxml) && FileUtils.isFileExists(danmuxml)) {
            try {
                inputStream = new FileInputStream(new File(danmuxml));

            } catch (FileNotFoundException e) {
                LogUtils.e("cuowu", e);
            }
        }
        //缩略图
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        Bitmap bitmap = retriever.getFrameAtTime();
        mViewSuperPlayer.mPlayerThumb.setImageBitmap(bitmap);
        //        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb); // Show the thumb before play
        mViewSuperPlayer.init()              // Initialize, the first to use
                .alwaysFullScreen()//始终全屏
//                .setSkipTip(1000 * 60 * 0)  // set the position you want to skip
                .enableOrientation()    // enable orientation
                .setVideoPath(videoPath)    // set video url
//                .setVideoSource(null, VIDEO_URL, VIDEO_URL, VIDEO_URL, null) // set multiple video url
//                .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)  // set the initial video url
                .enableDanmaku()        // enable Danmaku
                .setDanmakuSource(inputStream)
                .showOrHideDanmaku(!hide_danmu)
                .setTitle(file_title)      // set title
                .setQualityButtonVisibility(false)//需要在全屏后使用生效
           ;


//        mViewSuperPlayer.setLive(false);//设置该地址是直播的地址
//        mViewSuperPlayer.setNetChangeListener(true)//设置监听手机网络的变化
//                .setOnNetChangeListener(this)//实现网络变化的回调
//                .onPrepared(new SuperPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared() {
//                        /**
//                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
//                         */
//                        playerPrepare = true;
//                        if (danmuPrepare) {
//                            mViewSuperPlayer.start();
//                            LogUtils.i("播放器开始");
//                            mViewSuperPlayer.getDanmakuView().resume();
//                            LogUtils.i("弹幕显示");
//                        }
//
//                    }
//                }).onComplete(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
//                 */
//            }
//        }).onInfo(new SuperPlayer.OnInfoListener() {
//            @Override
//            public void onInfo(int what, int extra) {
//                /**
//                 * 监听视频的相关信息。
//                 */
//
//            }
//        }).onError(new SuperPlayer.OnErrorListener() {
//            @Override
//            public void onError(int what, int extra) {
//                /**
//                 * 监听视频播放失败的回调
//                 */
//
//            }
//        }).onDanmuViewPrepared(new SuperPlayer.OnDanmuViewPreparedListener() {
//            @Override
//            public void onPrepared() {
//                danmuPrepare = true;
//                if (playerPrepare) {
//                    mViewSuperPlayer.start();
//                    LogUtils.i("播放器开始");
//                    mViewSuperPlayer.getDanmakuView().resume();
//                    LogUtils.i("弹幕显示");
//                }
//
//            }
//        });
//        ;
//        mViewSuperPlayer.initDanmuView(videoPath, hide_danmu);
//        mViewSuperPlayer.setScaleType(SuperPlayer.SCALETYPE_FITXY);
//        mViewSuperPlayer.setPlayerWH(0, mViewSuperPlayer.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
    }


    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void error() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewSuperPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewSuperPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewSuperPlayer.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mViewSuperPlayer.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mViewSuperPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}

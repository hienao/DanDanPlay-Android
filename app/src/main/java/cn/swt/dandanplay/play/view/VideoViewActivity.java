package cn.swt.dandanplay.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.superplayer.library.SuperPlayer;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
import cn.swt.dandanplay.play.contract.VideoViewContract;
import cn.swt.dandanplay.play.presenter.VideoViewPresenter;

public class VideoViewActivity extends AppCompatActivity implements VideoViewContract.View, View.OnClickListener, SuperPlayer.OnNetChangeListener {
    @Inject
    VideoViewPresenter mVideoViewPresenter;
    @BindView(R.id.activity_video_view)
    RelativeLayout     mActivityVideoView;
    @BindView(R.id.view_super_player)
    SuperPlayer        mViewSuperPlayer;
    private String            videoPath;
    private String            videoTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        ButterKnife.bind(this);
        initData();
        initView();
        initPlayer();
        mVideoViewPresenter.matchEpisodeId(videoPath, videoTitle, getVideoFileHash(videoPath), String.valueOf(new File(videoPath).length()), String.valueOf(getVideoDuration(videoPath)), "0");
    }

    /**
     * 文件前16MB(16x1024x1024Byte)数据的32位MD5结果，不区分大小写
     * 若小于此大小则返回文件的MD5
     *
     * @return
     */
    @Override
    public String getVideoFileHash(String filePath) {
        try {
            File file = FileUtils.getFileByPath(filePath);
            if (file.length() < 16 * 1024 * 1024) {
                return MD5Util.getFileMD5String(file);
            }
            else {
                RandomAccessFile r = new RandomAccessFile(file, "r");
                r.seek(0);
                byte[] bs = new byte[16 * 1024 * 1024];
                r.read(bs);
                return MD5Util.getMD5String(bs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取视频时长
     *
     * @param path 路径
     * @return 时长
     */
    @Override
    public long getVideoDuration(String path) {
        long result = 0;
        File f = new File(path);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(new FileInputStream(f).getFD());
            mediaPlayer.prepare();
            result = mediaPlayer.getDuration();
        } catch (IOException e) {
            LogUtils.i(e.toString());
            e.printStackTrace();
        } finally {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return result;
    }


    @Override
    public void addBiliBiliDanmu(String a0, String a1, String a2, String a3, String a4, String a5, String a6, String a7,String text) {
        mViewSuperPlayer.addBiliBiliDanmu(a0, a1, a2, a3, a4, a5, a6, a7, text);
    }


    private void initData() {
        videoPath = getIntent().getStringExtra("path");
        videoTitle = getIntent().getStringExtra("title");
        mVideoViewPresenter = new VideoViewPresenter(this);
    }

    private void initView() {
    }

    private void initPlayer() {
        mViewSuperPlayer.setLive(false);//设置该地址是直播的地址
        mViewSuperPlayer.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        }).setTitle(videoTitle)//设置视频的titleName
                .play(videoPath);//开始播放视频
        mViewSuperPlayer.setScaleType(SuperPlayer.SCALETYPE_FITXY);
        mViewSuperPlayer.setPlayerWH(0, mViewSuperPlayer.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void gotMatchEpisodeId(MatchResponse matchResponse) {
        if (matchResponse.getMatches() == null || matchResponse.getMatches().size() == 0) {

        } else {
            MatchResponse.MatchesBean matchesBean = matchResponse.getMatches().get(0);
            mViewSuperPlayer.setTitle(matchesBean.getAnimeTitle() + " " + matchesBean.getEpisodeTitle());
            mVideoViewPresenter.getComment(String.valueOf(matchesBean.getEpisodeId()), "0");
            mVideoViewPresenter.getCommentSource(String.valueOf(matchesBean.getEpisodeId()));
        }
    }

    @Override
    public void gotComment(CommentResponse commentResponse) {
        if (commentResponse.getComments() == null || commentResponse.getComments().size() == 0) {

        } else {
            List<CommentResponse.CommentsBean> commentsBeanList = commentResponse.getComments();
        }
    }

    @Override
    public void error() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewSuperPlayer != null) {
            mViewSuperPlayer.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewSuperPlayer != null) {
            mViewSuperPlayer.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewSuperPlayer != null) {
            mViewSuperPlayer.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mViewSuperPlayer != null) {
            mViewSuperPlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewSuperPlayer != null && mViewSuperPlayer.onBackPressed()) {
            finish();
//            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
//        if(view.getId() == R.id.tv_replay){
//            if(player != null){
//                player.play(url);
//            }
//        } else if(view.getId() == R.id.tv_play_location){
//            if(isLive){
//                Toast.makeText(this,"直播不支持指定播放",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(player != null){
//                /**
//                 * 这个节点是根据视频的大小来获取的。不同的视频源节点也会不一致（一般用在退出视频播放后保存对应视频的节点从而来达到记录播放）
//                 */
//                player.play(url,89528);
//            }
//        } else if(view.getId() == R.id.tv_play_switch) {
//            /**
//             * 切换视频播放源（一般是标清，高清的切换ps：由于我没有找到高清，标清的视频源，所以也是换相同的地址）
//             */
//            if(isLive){
//                player.playSwitch(url);
//            } else {
//                player.playSwitch("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
//            }
//        }
    }

    @Override
    public void onWifi() {

    }

    @Override
    public void onMobile() {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onNoAvailable() {

    }
}

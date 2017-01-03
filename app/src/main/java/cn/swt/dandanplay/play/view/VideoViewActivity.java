package cn.swt.dandanplay.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
import cn.swt.dandanplay.play.contract.VideoViewContract;
import cn.swt.dandanplay.play.presenter.VideoViewPresenter;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class VideoViewActivity extends AppCompatActivity implements VideoViewContract.View, View.OnClickListener, SuperPlayer.OnNetChangeListener {
    @Inject
    VideoViewPresenter mVideoViewPresenter;
    @BindView(R.id.activity_video_view)
    RelativeLayout     mActivityVideoView;
    @BindView(R.id.view_super_player)
    SuperPlayer        mViewSuperPlayer;
    @BindView(R.id.danmaku_view)
    DanmakuView        mDanmakuView;
    private DanmakuContext     mDanmakuContext;
    private BaseDanmakuParser mBaseDanmakuParser;
    private List<BaseDanmaku> mCommentsBeanList;
    private String            videoPath;
    private String            videoTitle;
    private boolean danMuShowState = true;


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
        BaseDanmaku danmaku = null;
        //设置弹幕模式
        switch (a1){
            case "1":
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
                break;
            case "4":
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_BOTTOM);
                break;
            case "5":
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_TOP);
                break;
            case "6":
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_LR);
                break;
            case "7":
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SPECIAL);
                break;
            default:
                danmaku=mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
                break;
        }
        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.textSize = biliFontSizeConvert(Integer.parseInt(a2));
        danmaku.textColor = Integer.parseInt(a3);
        danmaku.setTime((long)(Double.parseDouble(a0)*1000));
        danmaku.priority=Byte.parseByte(a5);
        danmaku.userHash=a6;
        try {
            danmaku.index=Integer.parseInt(a7);
        }catch (Exception e){
            //超出int范围index  直接放弃
        }
        mDanmakuView.addDanmaku(danmaku);
    }


    private void initData() {
        videoPath = getIntent().getStringExtra("path");
        videoTitle = getIntent().getStringExtra("title");
        mVideoViewPresenter = new VideoViewPresenter(this);
        mBaseDanmakuParser=new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
        mCommentsBeanList=new ArrayList<>();
    }

    private void initView() {
        mDanmakuView.enableDanmakuDrawingCache(true);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mDanmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        mDanmakuContext=DanmakuContext.create();
        // 设置弹幕的最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 16); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);

        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) //设置描边样式
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //是否启用合并重复弹幕
                .setScaleTextSize(1.2f) //设置弹幕滚动速度系数,只对滚动弹幕有效
                .setMaximumLines(maxLinesPair) //设置最大显示行数
                .preventOverlapping(overlappingEnablePair); //设置防弹幕重叠，null为允许重叠
        mDanmakuView.prepare(mBaseDanmakuParser,mDanmakuContext);
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
        mViewSuperPlayer.setOnShowDanMuState(new SuperPlayer.OnDanMuClickListener() {
            @Override
            public void setDanMuClick() {
                if (danMuShowState){
                    danMuShowState=false;
                    mDanmakuView.hide();
                }else {
                    danMuShowState=true;
                    mDanmakuView.show();
                }
            }
        });
        mViewSuperPlayer.setOnSeek(new SuperPlayer.OnVideoSeekListener() {
            @Override
            public void seekChange(long newPosion) {
                mDanmakuView.seekTo(newPosion);
            }
        });
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
    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = biliFontSizeConvert(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(mDanmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        mDanmakuView.addDanmaku(danmaku);
        //mDanmakuView.seekTo((long)mViewSuperPlayer.getCurrentPosition());
    }


    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (danMuShowState){
                        int time = new Random().nextInt(300);
                        String content = "" + time + time;
                        addDanmaku(content, false);
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * b站弹幕字体大小转换
     */
    public int biliFontSizeConvert(float pxValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue * fontScale * 0.6 + 0.5f);
    }
}

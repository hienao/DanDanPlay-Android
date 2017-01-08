package cn.swt.dandanplay.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.superplayer.library.SuperPlayer;
import com.swt.corelib.utils.ProgressDialogUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.play.contract.VideoViewContract;
import cn.swt.dandanplay.play.presenter.VideoViewPresenter;

public class VideoViewActivity extends AppCompatActivity implements VideoViewContract.View, View.OnClickListener, SuperPlayer.OnNetChangeListener {
    @Inject
    VideoViewPresenter mVideoViewPresenter;
    @BindView(R.id.activity_video_view)
    RelativeLayout     mActivityVideoView;
    @BindView(R.id.view_super_player)
    SuperPlayer        mViewSuperPlayer;
    private String            videoPath,videoTitle,file_title;
    private int            episode_id;
    private boolean gotDanDanPlayComment=false;//是否加载完dandanplay的弹幕源
    private int otherCommentSourceNum=-1,otherCommentSourceCount=0;//第三方弹幕源数量，已加载第三方弹幕源数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        ButterKnife.bind(this);
        initData();
        initPlayer();
        initView();
    }


    @Override
    public void addBiliBiliDanmu(String a0, String a1, String a2, String a3, String a4, String a5, String a6, String a7,String text) {
        mViewSuperPlayer.addBiliBiliDanmu(a0, a1, a2, a3, a4, a5, a6, a7, text);
    }


    private void initData() {
        videoPath = getIntent().getStringExtra("path");
        file_title = getIntent().getStringExtra("file_title");
        videoTitle = getIntent().getStringExtra("title");
        episode_id =getIntent().getIntExtra("episode_id",-1);
        mVideoViewPresenter = new VideoViewPresenter(this);
    }

    private void initView() {
        if (!TextUtils.isEmpty(videoTitle)){
            mViewSuperPlayer.setTitle(videoTitle);
        }else {
            mViewSuperPlayer.setTitle(file_title);
        }
        mViewSuperPlayer.play(videoPath);
        mViewSuperPlayer.pause();
        if (mViewSuperPlayer.getDanmakuView()!=null)
            mViewSuperPlayer.getDanmakuView().hide();
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
        });
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
    public void gotComment(CommentResponse commentResponse) {
        if (commentResponse==null || commentResponse.getComments() == null || commentResponse.getComments().size() == 0) {
        } else {
            List<CommentResponse.CommentsBean> commentsBeanList = commentResponse.getComments();
            if (commentsBeanList!=null&&commentsBeanList.size()!=0){
                for (CommentResponse.CommentsBean commentsBean:commentsBeanList){
                    mViewSuperPlayer.addBiliBiliDanmu(String.valueOf(commentsBean.getTime()), String.valueOf(commentsBean.getMode()),
                            String.valueOf(25), String.valueOf(commentsBean.getColor()),"",
                            String.valueOf(commentsBean.getPool()),String.valueOf(commentsBean.getUId()),
                            String.valueOf(commentsBean.getCId()),commentsBean.getMessage());
                }
            }
        }
        gotDanDanPlayComment=true;
        judgeDanmuLoadState();
    }

    @Override
    public void setOtherCommentSourceNum(int num) {
        otherCommentSourceNum=num;
    }

    @Override
    public void addOtherCommentSourceCount() {
        otherCommentSourceCount++;
        judgeDanmuLoadState();
    }

    private void judgeDanmuLoadState() {
        if (gotDanDanPlayComment&&otherCommentSourceNum>-1&&otherCommentSourceNum<=otherCommentSourceCount){
            loadFinish();
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
        if (episode_id>0){
            ProgressDialogUtils.showDialog(VideoViewActivity.this,getResources().getString(R.string.danmu_loading));
            mVideoViewPresenter.getComment(String.valueOf(episode_id), "0");
            mVideoViewPresenter.getCommentSource(String.valueOf(episode_id));
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
    private void loadFinish(){
        ProgressDialogUtils.dismissDialog();
        mViewSuperPlayer.start();
        if (mViewSuperPlayer.getDanmakuView()!=null) {
            mViewSuperPlayer.getDanmakuView().show();
        }
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

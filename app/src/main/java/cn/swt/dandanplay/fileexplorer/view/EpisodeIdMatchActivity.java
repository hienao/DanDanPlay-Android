package cn.swt.dandanplay.fileexplorer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.core.base.BaseActivity;
import cn.swt.dandanplay.core.http.beans.MatchResponse;
import cn.swt.dandanplay.fileexplorer.component.DaggerMainComponent;
import cn.swt.dandanplay.fileexplorer.contract.EpisodeIdMatchContract;
import cn.swt.dandanplay.fileexplorer.module.MainModule;
import cn.swt.dandanplay.fileexplorer.presenter.EpisodeIdMatchPresenter;

public class EpisodeIdMatchActivity extends BaseActivity implements EpisodeIdMatchContract.View {
    @Inject
    EpisodeIdMatchPresenter mEpisodeIdMatchPresenter;
    private String videoPath;
    private String videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_id_match);
        ButterKnife.bind(this);
        DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        initData();
        initView();
        mEpisodeIdMatchPresenter.matchEpisodeId(videoPath, videoTitle, getVideoFileHash(videoPath), String.valueOf(new File(videoPath).length()), String.valueOf(getVideoDuration(videoPath)), "0");
    }

    private void initData() {
        videoPath = getIntent().getStringExtra("path");
        videoTitle = getIntent().getStringExtra("title");
//        mEpisodeIdMatchPresenter=new EpisodeIdMatchPresenter(this);
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.danmu_match));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void gotMatchEpisodeId(MatchResponse matchResponse) {
        if (matchResponse.getMatches() == null || matchResponse.getMatches().size() == 0) {
            //未检测到，提示用户手工匹配
        } else {
            //检测到，提示用户判断是否正确，错误的话手工匹配
            MatchResponse.MatchesBean matchesBean = matchResponse.getMatches().get(0);
        }
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
            if (file!=null){
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
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void error() {

    }
}

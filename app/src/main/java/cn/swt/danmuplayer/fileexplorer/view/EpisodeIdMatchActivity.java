package cn.swt.danmuplayer.fileexplorer.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.MD5Util;
import com.swt.corelib.utils.ProgressDialogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.core.base.BaseActivity;
import cn.swt.danmuplayer.core.http.beans.MatchResponse;
import cn.swt.danmuplayer.fileexplorer.adapter.MatchResultAdapter;
import cn.swt.danmuplayer.fileexplorer.beans.SearchResultInfo;
import cn.swt.danmuplayer.fileexplorer.component.DaggerMainComponent;
import cn.swt.danmuplayer.fileexplorer.contract.EpisodeIdMatchContract;
import cn.swt.danmuplayer.fileexplorer.module.MainModule;
import cn.swt.danmuplayer.fileexplorer.presenter.EpisodeIdMatchPresenter;
import cn.swt.danmuplayer.fileexplorer.utils.DanmuUtils;
import cn.swt.danmuplayer.play.view.VideoViewActivity;

public class EpisodeIdMatchActivity extends BaseActivity implements EpisodeIdMatchContract.View {
    @Inject
    EpisodeIdMatchPresenter mEpisodeIdMatchPresenter;
    @BindView(R.id.rv_match_results)
    RecyclerView mRvMatchResults;
    @BindView(R.id.edt_search_episode_title)
    EditText mEdtSearchEpisodeTitle;
    @BindView(R.id.edt_search_episode_id)
    EditText mEdtSearchEpisodeId;
    @BindView(R.id.btn_episode_skip)
    Button mBtnEpisodeSkip;
    @BindView(R.id.btn_episode_search)
    Button mBtnEpisodeSearch;
    @BindView(R.id.webview)
    WebView mWebview;
    @BindView(R.id.activity_episode_id_match)
    LinearLayout mActivityEpisodeIdMatch;
    private String videoPath;
    private String videoTitle;
    private List<SearchResultInfo> mSearchBeanList;
    private MatchResultAdapter mMatchResultAdapter;

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
        inintListener();
    }

    private void initData() {
        videoPath = getIntent().getStringExtra("path");
        videoTitle = getIntent().getStringExtra("title");
        mSearchBeanList = new ArrayList<>();
        mMatchResultAdapter = new MatchResultAdapter(this, mSearchBeanList, videoPath, videoTitle);
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.danmu_match));
        mRvMatchResults.setItemAnimator(new DefaultItemAnimator());
        mRvMatchResults.setLayoutManager(new LinearLayoutManager(this));
        mRvMatchResults.setAdapter(mMatchResultAdapter);
        ProgressDialogUtils.showDialog(EpisodeIdMatchActivity.this, getResources().getString(R.string.loading));
        mEpisodeIdMatchPresenter.matchEpisodeId(videoPath, videoTitle, getVideoFileHash(videoPath), String.valueOf(new File(videoPath).length()), String.valueOf(getVideoDuration(videoPath)), "0");
    }

    private void inintListener() {
        mBtnEpisodeSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnEpisodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBeanList.clear();
                ProgressDialogUtils.showDialog(EpisodeIdMatchActivity.this, getResources().getString(R.string.loading));
                mEpisodeIdMatchPresenter.searchALLEpisodeId(mEdtSearchEpisodeTitle.getText().toString(), mEdtSearchEpisodeId.getText().toString());

            }
        });
        mBtnEpisodeSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EpisodeIdMatchActivity.this, VideoViewActivity.class)
                        .putExtra("path", videoPath)
                        .putExtra("file_title", videoTitle).putExtra("hide_danmu", true));
            }
        });
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void gotMatchEpisodeId(MatchResponse matchResponse) {
        dismissProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(getResources().getString(R.string.match_result)); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        if (matchResponse.getMatches() == null || matchResponse.getMatches().size() == 0) {
            //未检测到，提示用户手工匹配调用关键字匹配接口
            builder.setMessage(getResources().getString(R.string.match_none)); //设置内容
            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //关闭dialog
                }
            });
            builder.create().show();
        } else {
            //检测到，提示用户判断是否正确，错误的话手工匹配
            final MatchResponse.MatchesBean matchesBean = matchResponse.getMatches().get(0);
            //弹窗提示
            builder.setMessage(getResources().getString(R.string.match_result) + ":" + matchesBean.getAnimeTitle() + "\r\n" + matchesBean.getEpisodeTitle()); //设置内容
            builder.setPositiveButton(getResources().getString(R.string.match_result_right), new DialogInterface.OnClickListener() { //设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //关闭dialog
                    ProgressDialogUtils.showDialog(EpisodeIdMatchActivity.this, getResources().getString(R.string.danmu_loading));
                    LogUtils.i("开始获取弹幕");
                    DanmuUtils danmuUtils =new DanmuUtils(EpisodeIdMatchActivity.this,videoPath,videoTitle,
                            matchesBean.getAnimeTitle() + " " + matchesBean.getEpisodeTitle(),matchesBean.getEpisodeId());
                    danmuUtils.getDanmuListByEspoisedId();

                }
            });
            builder.setNegativeButton(getResources().getString(R.string.match_result_wrong), new DialogInterface.OnClickListener() { //设置取消按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            List<MatchResponse.MatchesBean> matchesBeanList = matchResponse.getMatches();
            for (MatchResponse.MatchesBean matchesBean1 : matchesBeanList) {
                SearchResultInfo resultInfoHeader = new SearchResultInfo();
                resultInfoHeader.setHeader(true);
                resultInfoHeader.setMainTitle(matchesBean1.getAnimeTitle());
                resultInfoHeader.setType(matchesBean1.getType());
                mSearchBeanList.add(resultInfoHeader);
                SearchResultInfo resultInfo = new SearchResultInfo();
                resultInfo.setHeader(false);
                resultInfo.setMainTitle(matchesBean1.getAnimeTitle());
                resultInfo.setType(matchesBean1.getType());
                resultInfo.setTitle(matchesBean1.getEpisodeTitle());
                resultInfo.setId(matchesBean1.getEpisodeId());
                mSearchBeanList.add(resultInfo);
            }
            mMatchResultAdapter.notifyDataSetChanged();
            //不正确调用关键字匹配接口
            if (videoTitle.contains(" ")) {
                if (TextUtils.isDigitsOnly(videoTitle.substring(videoTitle.lastIndexOf(" ") + 1))) {
                    mEdtSearchEpisodeId.setText(videoTitle.substring(videoTitle.lastIndexOf(" ") + 1));
                    mEdtSearchEpisodeTitle.setText(videoTitle.substring(0, videoTitle.lastIndexOf(" ")));
                } else {
                    mEdtSearchEpisodeTitle.setText(videoTitle);
                }
            } else {
                mEdtSearchEpisodeTitle.setText(videoTitle);
            }
        }
    }

    /**
     * 获取到人工输入信息的搜索结果
     *
     * @param searchResultInfo
     */
    @Override
    public void gotSearchALLEpisodeId(List<SearchResultInfo> searchResultInfo) {
        dismissProgressDialog();
        mSearchBeanList.clear();
        if (searchResultInfo != null && searchResultInfo.size() != 0) {
            mSearchBeanList.addAll(searchResultInfo);
        }
        mMatchResultAdapter.notifyDataSetChanged();
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
            if (file != null) {
                if (file.length() < 16 * 1024 * 1024) {
                    return MD5Util.getFileMD5String(file);
                } else {
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
    public void dismissProgressDialog() {
        ProgressDialogUtils.dismissDialog();
    }

    public WebView getWebview(){
        return mWebview;
    }



    @Override
    public void error() {

    }
}

package cn.swt.dandanplay.fileexplorer.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.application.MyApplication;
import cn.swt.dandanplay.core.base.BaseActivity;
import cn.swt.dandanplay.fileexplorer.adapter.FileAdapter;
import cn.swt.dandanplay.fileexplorer.beans.VideoFileInfo;
import cn.swt.dandanplay.fileexplorer.component.DaggerMainComponent;
import cn.swt.dandanplay.fileexplorer.contract.FileExplorerContract;
import cn.swt.dandanplay.fileexplorer.module.MainModule;
import cn.swt.dandanplay.fileexplorer.presenter.FileExplorerPresenter;

public class FileExplorerActivity extends BaseActivity implements FileExplorerContract.View{
    @Inject
    FileExplorerPresenter mFileExplorerPresenter;
    @BindView(R.id.rv_files)
    RecyclerView mRvFiles;
    private List<VideoFileInfo> mDatas;
    private FileAdapter mFileAdapter;
    private String contentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        ButterKnife.bind(this);
        DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mDatas=new ArrayList<>();
        mFileAdapter=new FileAdapter(this,mDatas);
        contentPath= getIntent().getStringExtra("contentpath");
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_name));
        mRvFiles.setItemAnimator(new DefaultItemAnimator());
        mRvFiles.setLayoutManager(new LinearLayoutManager(this));
        mRvFiles.setAdapter(mFileAdapter);
        if (!TextUtils.isEmpty(contentPath)){
            getDataFromSQLite();
        }
    }

    private void initListener() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void error() {

    }

    @Override
    public void setData(List<VideoFileInfo> list) {
        mDatas.clear();
        mDatas.addAll(list);
        for (VideoFileInfo videoFileInfo:mDatas){
            LogUtils.i(videoFileInfo.toString());
        }
        mFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDataFromSQLite() {
        mDatas.clear();
        ArrayList<VideoFileInfo>list=MyApplication.getLiteOrm().query(VideoFileInfo.class);
        QueryBuilder<VideoFileInfo>qb=new QueryBuilder<>(VideoFileInfo.class).whereEquals("_contentPath",contentPath);
        ArrayList<VideoFileInfo> videoFileInfoArrayList = MyApplication.getLiteOrm().query(qb);
        if (videoFileInfoArrayList != null&&videoFileInfoArrayList.size()!=0){
            for (VideoFileInfo v:videoFileInfoArrayList){
                //更新数据库记录为有本地弹幕
                VideoFileInfo vv = MyApplication.getLiteOrm().queryById(v.getVideoPath(), VideoFileInfo.class);
                if (vv!=null){
                    String ddxml=vv.getVideoPath().substring(0,vv.getVideoPath().lastIndexOf("."))+"dd.xml";
                    String bilixml=vv.getVideoPath().substring(0,vv.getVideoPath().lastIndexOf("."))+".xml";
                    if (FileUtils.isFileExists(ddxml)||FileUtils.isFileExists(bilixml)){
                        vv.setHaveLocalDanmu(true);
                        v.setHaveLocalDanmu(true);
                    }
                }
                MyApplication.getLiteOrm().save(v);
                //更新数据库记录为有本地弹幕
            }
            mDatas.addAll(videoFileInfoArrayList);
        }
        mFileAdapter.notifyDataSetChanged();
    }
}

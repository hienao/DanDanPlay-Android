package cn.swt.danmuplayer.fileexplorer.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.base.BaseActivity;
import cn.swt.danmuplayer.fileexplorer.adapter.FileAdapter;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.contract.FileExplorerContract;
import cn.swt.danmuplayer.fileexplorer.presenter.FileExplorerPresenter;
import io.realm.Realm;
import io.realm.RealmResults;

public class FileExplorerActivity extends BaseActivity implements FileExplorerContract.View {
    FileExplorerPresenter mFileExplorerPresenter;
    @BindView(R.id.rv_files)
    RecyclerView mRvFiles;
    @BindView(R.id.stool_toolbar)
    Toolbar mStoolToolbar;
    private List<VideoFileInfo> mDatas;
    private FileAdapter mFileAdapter;
    private String contentPath;
    private boolean mNetworkMode=true;
    private Realm realm ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        realm = MyApplication.getRealmInstance();
        mFileExplorerPresenter=new FileExplorerPresenter(this);
        mDatas = new ArrayList<>();
        mFileAdapter = new FileAdapter(this, mDatas);
        contentPath = getIntent().getStringExtra("contentpath");
        mNetworkMode=MyApplication.getSP().getBoolean("network_mode",false);
        mFileAdapter.setNetworkMode(mNetworkMode);
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_name));
        mRvFiles.setItemAnimator(new DefaultItemAnimator());
        mRvFiles.setLayoutManager(new LinearLayoutManager(this));
        mRvFiles.setAdapter(mFileAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(contentPath)) {
            getDataFromSQLite();
        }
    }

    private void initListener() {
        mStoolToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_switch_network_mode:
                        mNetworkMode=!mNetworkMode;
                        if (mNetworkMode){
                            item.setIcon(R.drawable.ic_online);
                            item.setTitle(R.string.switch_network_online);
                            ToastUtils.showShortToastSafe(FileExplorerActivity.this,getResources().getString(R.string.switch_network_online));
                        }else {
                            item.setIcon(R.drawable.ic_offline);
                            item.setTitle(R.string.switch_network_offline);
                            ToastUtils.showShortToastSafe(FileExplorerActivity.this,getResources().getString(R.string.switch_network_offline));
                        }
                        MyApplication.getSP().putBoolean("network_mode",mNetworkMode);
                        mFileAdapter.setNetworkMode(mNetworkMode);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mNetworkMode) {
            menu.findItem(R.id.action_switch_network_mode).setIcon(R.drawable.ic_online).setTitle(R.string.switch_network_online);
        }else {
            menu.findItem(R.id.action_switch_network_mode).setIcon(R.drawable.ic_offline).setTitle(R.string.switch_network_offline);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_switch_network, menu);
        return true;
    }

    @Override
    public void error(String msg) {

    }

    @Override
    public void setData(List<VideoFileInfo> list) {
        mDatas.clear();
        mDatas.addAll(list);
        for (VideoFileInfo videoFileInfo : mDatas) {
            LogUtils.i(videoFileInfo.toString());
        }
        mFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDataFromSQLite() {
        mDatas.clear();
        RealmResults<VideoFileInfo> videoFileInfos = realm.where(VideoFileInfo.class).equalTo("videoContentPath",contentPath).findAll();
        List<VideoFileInfo> videolist = realm.copyFromRealm(videoFileInfos);
        if (videolist != null && videolist.size() != 0) {
            mDatas.addAll(videolist);
        }
        Collections.reverse(mDatas);
        mFileAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileExplorerPresenter=null;
    }
}

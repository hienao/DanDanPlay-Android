package cn.swt.danmuplayer.fileexplorer.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nightonke.boommenu.BoomMenuButton;
import com.swt.corelib.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.base.BaseActivity;
import cn.swt.danmuplayer.fileexplorer.adapter.ContentAdapter;
import cn.swt.danmuplayer.fileexplorer.beans.ContentInfo;
import cn.swt.danmuplayer.fileexplorer.contract.MainContract;
import cn.swt.danmuplayer.fileexplorer.presenter.MainPresenter;
import cn.swt.danmuplayer.fileexplorer.utils.BmbUtil;
import cn.swt.danmuplayer.setting.AddVideoFileManualActivity;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.tendcloud.tenddata.ab.mContext;


public class ContentsActivity extends BaseActivity implements MainContract.View {

    MainPresenter mMainPresenter;
    public static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 111;
    @BindView(R.id.rv_content)
    RecyclerView mRvContent;
    @BindView(R.id.store_house_ptr_frame)
    PtrFrameLayout mStoreHousePtrFrame;
    @BindView(R.id.stool_toolbar)
    Toolbar mStoolToolbar;
    @BindView(R.id.bmb)
    BoomMenuButton mBmb;
    private List<ContentInfo> mDatas;
    private ContentAdapter mContentAdapter;
    private ContentResolver mContentResolver;
    private String refreshHeader;
    private StoreHouseHeader mHeader;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromSQLite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //通知媒体库更新文件夹
//        notifyFileSystemChanged(Environment.getExternalStorageDirectory().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter = null;
    }

    private void initData() {
        realm = MyApplication.getRealmInstance();
        mMainPresenter = new MainPresenter(this);
        mDatas = new ArrayList<>();
        refreshHeader = getResources().getString(R.string.scaning);
        mContentAdapter = new ContentAdapter(this, mDatas);
        mContentResolver = this.getContentResolver();
        mHeader = new StoreHouseHeader(this);
        mHeader.setPadding(0, 15, 0, 0);
        mHeader.setTextColor(R.color.text_black);
        mHeader.initWithString(refreshHeader);
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        final File f = new File(path);
        if (Build.VERSION.SDK_INT >= 19 /*Build.VERSION_CODES.KITKAT*/) { //添加此判断，判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{path};
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        } else {
            final Intent intent;
            if (f.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(path)));
            }
            mContext.sendBroadcast(intent);
        }
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_name));
        setShowBackNavigationIcon(false);
        //菜单初始化开始
        mBmb = (BoomMenuButton) findViewById(R.id.bmb);
        BmbUtil.initBoomMenuButton(mBmb, ContentsActivity.this);
        //菜单初始化结束
        mStoreHousePtrFrame.setHeaderView(mHeader);
        mStoreHousePtrFrame.addPtrUIHandler(mHeader);
        mStoreHousePtrFrame.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {
                mHeader.initWithString(refreshHeader);
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });
        mRvContent.setItemAnimator(new DefaultItemAnimator());
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        mRvContent.setAdapter(mContentAdapter);
    }

    private void initListener() {
        mStoreHousePtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                requestPremission();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !frame.isRefreshing();
            }
        });
        mStoolToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        startActivity(new Intent(ContentsActivity.this, AddVideoFileManualActivity.class));
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
    public void error(String msg) {

    }

    @Override
    public void getDataFromSQLite() {
        mDatas.clear();
        RealmResults<ContentInfo> contentInfos = realm.where(ContentInfo.class).findAll();
        List<ContentInfo> contentInfoArrayList = realm.copyFromRealm(contentInfos);
        if (contentInfoArrayList != null) {
            mDatas.addAll(contentInfoArrayList);
        }
        mContentAdapter.notifyDataSetChanged();
        if (mDatas.size() == 0) {
            ToastUtils.showShortToast(ContentsActivity.this, R.string.no_file_notice);
        }
        mStoreHousePtrFrame.refreshComplete();
    }

    /**
     * 请求文件存储权限,获取媒体文件列表
     */
    void requestPremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            mMainPresenter.getAllVideo(mContentResolver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                getDataFromSQLite();
            } else {
                // Permission Denied
                ToastUtils.showShortToastSafe(this, "无法读取外置存储数据，请授予外置存储访问权限");
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_add, menu);
        return true;
    }

}

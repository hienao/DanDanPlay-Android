package cn.swt.danmuplayer.fileexplorer.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.swt.corelib.utils.ToastUtils;

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
import cn.swt.danmuplayer.fileexplorer.utils.BuilderManager;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;



public class ContentsActivity extends BaseActivity implements MainContract.View {

    MainPresenter mMainPresenter;
    public static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 111;
    @BindView(R.id.rv_content)
    RecyclerView mRvContent;
    @BindView(R.id.store_house_ptr_frame)
    PtrFrameLayout mStoreHousePtrFrame;
    private List<ContentInfo> mDatas;
    private ContentAdapter mContentAdapter;
    private ContentResolver mContentResolver;
    private String refreshHeader;
    private StoreHouseHeader mHeader;
    //菜单
    private BoomMenuButton bmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
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

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_name));
        setShowBackNavigationIcon(false);
        //菜单初始化开始
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                switch (index){
                    case 2:
                        ToastUtils.showShortToastSafe(ContentsActivity.this,"点击了设置按钮");
                        break;
                    default:
                        ToastUtils.showShortToastSafe(ContentsActivity.this,"点击了其他按钮");
                        break;
                }
            }

            @Override
            public void onBackgroundClick() {

            }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() {

            }

            @Override
            public void onBoomWillShow() {

            }

            @Override
            public void onBoomDidShow() {

            }
        });
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
        requestPremission();
    }

    private void initListener() {
        mStoreHousePtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mMainPresenter.getAllVideo(mContentResolver);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromSQLite();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void getDataFromSQLite() {
        mDatas.clear();
        ArrayList<ContentInfo> contentInfoArrayList = MyApplication.getLiteOrm().query(ContentInfo.class);
        if (contentInfoArrayList != null) {
            mDatas.addAll(contentInfoArrayList);
        }
        mContentAdapter.notifyDataSetChanged();
        if (mDatas.size() == 0) {
            ToastUtils.showShortToast(ContentsActivity.this, R.string.no_file_notice);
        }
        mStoreHousePtrFrame.refreshComplete();
    }

    @Override
    public void error() {

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
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter = null;
    }

}

package cn.swt.danmuplayer.fileexplorer.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swt.corelib.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.fileexplorer.adapter.ContentAdapter;
import cn.swt.danmuplayer.fileexplorer.beans.ContentInfo;
import cn.swt.danmuplayer.fileexplorer.contract.MainContract;
import cn.swt.danmuplayer.fileexplorer.presenter.ContentPresenter;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import io.realm.Realm;
import io.realm.RealmResults;


public class ContentsFragment extends Fragment implements MainContract.View {

    ContentPresenter mContentPresenter;
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
    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contents, container, false);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDataFromSQLite();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentPresenter = null;
    }

    private void initData() {
        realm = MyApplication.getRealmInstance();
        mContentPresenter = new ContentPresenter(this);
        mDatas = new ArrayList<>();
        refreshHeader = getResources().getString(R.string.scaning);
        mContentAdapter = new ContentAdapter(getContext(), mDatas);
        mContentResolver = getContext().getContentResolver();
        mHeader = new StoreHouseHeader(getContext());
        mHeader.setPadding(0, 15, 0, 0);
        mHeader.setTextColor(R.color.text_black);
        mHeader.initWithString(refreshHeader);
    }

    private void initView() {
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
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
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
    }

    @Override
    public Context getContext() {
        return getActivity();
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
            ToastUtils.showShortToast(getContext(), R.string.no_file_notice);
        }
        mStoreHousePtrFrame.refreshComplete();
    }

    /**
     * 请求文件存储权限,获取媒体文件列表
     */
    void requestPremission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            mContentPresenter.getAllVideo(mContentResolver);
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
                ToastUtils.showShortToastSafe(getContext(), "无法读取外置存储数据，请授予外置存储访问权限");
//                finish();
            }
        }
    }


}

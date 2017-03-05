package cn.swt.danmuplayer.fileexplorer.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
                mContentPresenter.getAllVideo();
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

}

package cn.swt.dandanplay.fileexplorer.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.swt.corelib.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.dandanplay.R;
import cn.swt.dandanplay.application.MyApplication;
import cn.swt.dandanplay.core.base.BaseActivity;
import cn.swt.dandanplay.fileexplorer.adapter.ContentAdapter;
import cn.swt.dandanplay.fileexplorer.beans.ContentInfo;
import cn.swt.dandanplay.fileexplorer.component.DaggerMainComponent;
import cn.swt.dandanplay.fileexplorer.contract.MainContract;
import cn.swt.dandanplay.fileexplorer.module.MainModule;
import cn.swt.dandanplay.fileexplorer.presenter.MainPresenter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentsActivity extends BaseActivity implements MainContract.View {

    @Inject
    MainPresenter mMainPresenter;
    @BindView(R.id.rv_content)
    RecyclerView  mRvContent;

    public static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 111;
    @BindView(R.id.swip_refesh_layout)
    SwipeRefreshLayout mSwipRefeshLayout;
    private List<ContentInfo> mDatas;
    private ContentAdapter    mContentAdapter;
    private ScanVideoFileReceiver mScanVideoFileReceiver = null;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        ButterKnife.bind(this);
        DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        initData();
        initView();
        initListener();
//        test();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mContentAdapter = new ContentAdapter(this, mDatas);
        //注册广播接收器
        mScanVideoFileReceiver = new ScanVideoFileReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("cn.swt.dandanplay.ScanVideoFileService");
        registerReceiver(mScanVideoFileReceiver, filter);
        mContentResolver = this.getContentResolver();
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_name));
        setShowNavigationIcon(false);
        mRvContent.setItemAnimator(new DefaultItemAnimator());
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        mRvContent.setAdapter(mContentAdapter);
        requestPremission();
    }

    private void initListener() {
        mSwipRefeshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainPresenter.getAllVideo(mContentResolver);
            }
        });
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
        if (mDatas.size()==0){
            ToastUtils.showShortToast(ContentsActivity.this,R.string.no_file_notice);
        }
        mSwipRefeshLayout.setRefreshing(false);
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
            getDataFromSQLite();
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
        unregisterReceiver(mScanVideoFileReceiver);
    }

    class ScanVideoFileReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean status = bundle.getBoolean("scanfinish");
            getDataFromSQLite();
        }
    }
    private void test(){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().addHeader("Accept","application/xhtml+xml,application/xml")
                .addHeader("Accept-Encoding","gzip, deflate").url("http://comment.bilibili.com/6065590.xml");
        final Request request = requestBuilder.build();
        okhttp3.Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    byte[] bytes = response.body().bytes(); //获取数据的bytes
//                    InputStream inputStream=new ByteArrayInputStream(bytes);
//                    FileUtils.writeFileFromIS("sdcard/test.xml",inputStream,true);
                    String content = new String(bytes,"GB18030");
//                    String content = response.body().string();
                    System.out.println(content);
                }
            }
        });
    }


}

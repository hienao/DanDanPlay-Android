package cn.swt.danmuplayer.core.http;

import android.content.Context;
import android.widget.Toast;

import com.swt.corelib.utils.NetworkUtils;

import java.util.concurrent.TimeUnit;

import cn.swt.danmuplayer.application.MyApplication;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Title: RetrofitManager <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/24 8:59
 * Created by Wentao.Shi.
 */
public class RetrofitManager {
    private  Context mContext = MyApplication.getMyApplicationContext();
    // 网络请求超时
    private static final int TIME_OUT = 10000;
    private Retrofit retrofit;

    public RetrofitManager() {
    }
    private static class InstanceHolder {
        private static final RetrofitManager instance = new RetrofitManager();
    }
    public static RetrofitManager getInstance() {
        return InstanceHolder.instance;
    }
    /**
     * 获取一个服务对象，使用Gson转换器，用于json数据的交互
     *
     * @return
     */
    public synchronized APIService create() {
        if (retrofit == null||!retrofit.baseUrl().equals(HttpConstant.ACPLAY_BASE_URL)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HttpConstant.ACPLAY_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return retrofit.create(APIService.class);
    }

    /**
     *获取一个服务对象，使用Gson转换器，用于json数据的交互
     * @param URL   基础URL
     * @return
     */
    public synchronized APIService create(String URL) {
        if (retrofit == null||!retrofit.baseUrl().equals(URL)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return retrofit.create(APIService.class);
    }
    /**
     * 获取一个服务对象，传入一个新的转换器，处理 流，原始的json字符串
     *
     * @param factory
     * @return
     */
    public APIService create(Converter.Factory factory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpConstant.ACPLAY_BASE_URL)
                .addConverterFactory(factory)
                .client(getClient())
                .build();
        return retrofit.create(APIService.class);
    }
    /**
     * 获取一个client对象
     *
     * @return
     */
    private OkHttpClient getClient() {
        //设置缓存目录
//        File dir = new File(CachePath.HTTPCACHE);
//        Cache cache = new Cache(dir, CACHE_SIZE);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
//                .cache(cache)
                .build();
        return client;
    }
    /**
     * 执行
     *
     * @param call
     * @param callback
     */
    public void enqueue(Call call, Callback callback) {
        if (!NetworkUtils.isConnected(MyApplication.getMyApplicationContext())) {
            Toast.makeText(mContext,"网络未连接", Toast.LENGTH_SHORT).show();
        }
        call.enqueue(callback);
    }
}

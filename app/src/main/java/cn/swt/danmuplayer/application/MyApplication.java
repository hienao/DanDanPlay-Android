package cn.swt.danmuplayer.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.SPUtils;
import com.swt.corelib.utils.ToastUtils;
import com.tencent.bugly.Bugly;
import com.tendcloud.tenddata.TCAgent;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Title: MyApplication <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 9:57
 * Created by Wentao.Shi.
 */
public class MyApplication extends Application {
    public static String TAG="SWTTAG";
    private static Context context;
    private static SPUtils mSpUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = this;
        //log开关
        if (isApkInDebug(context)){
            LogUtils.init(context,true,true,'v',TAG);
        }else {
            LogUtils.init(context,false,true,'e',TAG);
        }
        //SP初始化
        mSpUtils = new SPUtils(context,"Setting");

        //bugly初始化
        Bugly.init(context, "92428c9315", false);
        //talkData初始化
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this);
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(false);
        //SmartTool初始化
//        SmartToolCore.getInstance().init(this);
        //toast初始化
        ToastUtils.init(false);
        //初始化realm数据库
        Realm.init(this);
        RealmConfiguration realmconfig = new RealmConfiguration.Builder()
                .name("danmuplayer")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmconfig);
    }
    public static Context getMyApplicationContext(){
        return context;
    }
    /**
     * 判断当前应用是否是debug状态
     */

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
    public static SPUtils getSP(){
        return mSpUtils;
    }
}

package cn.swt.dandanplay.application;

import android.app.Application;
import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.swt.corelib.utils.ToastUtils;
import com.tencent.bugly.Bugly;

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
    private static LiteOrm liteOrm;
    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();
        //bugly初始化
        Bugly.init(getApplicationContext(), "92428c9315", false);

        //toast初始化
        ToastUtils.init(false);
        //初始化liteorm
        if (liteOrm == null) {
            DataBaseConfig config = new DataBaseConfig(this, "liteorm.db");
            config.debugged = true; // open the log
            config.dbVersion = 1; // set database version
            config.onUpdateListener = null; // set database update listener
            liteOrm = LiteOrm.newSingleInstance(config);
        }
    }

    public static LiteOrm getLiteOrm(){
        return liteOrm;
    }
    public static Context getMyApplicationContext(){
        return context;
    }
}

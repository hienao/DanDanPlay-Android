package cn.swt.danmuplayer.fileexplorer.contract;

import android.content.Context;

import cn.swt.danmuplayer.core.base.BasePresenter;
import cn.swt.danmuplayer.core.base.BaseView;

/**
 * Title: MainContract <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2016 <br>
 * Created DateTime: 2016/10/17 10:43
 * Created by Wentao.Shi.
 */
public class MainContract {
    public interface View extends BaseView{
        Context getContext();
        void getDataFromSQLite();
    }

    public interface Present extends BasePresenter {
        void getAllVideo();
    }
}

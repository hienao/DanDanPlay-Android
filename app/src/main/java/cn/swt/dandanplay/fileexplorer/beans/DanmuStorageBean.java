package cn.swt.dandanplay.fileexplorer.beans;

import java.util.List;

/**
 * Title: cn.swt.dandanplay.fileexplorer.beans.DanmuStorageBean <br>
 * Description: 本地弹幕缓存数据<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-10 15:27
 * Created by Wentao.Shi.
 */
public class DanmuStorageBean {
    List<DanmakuBean> mDanmuBeanList;

    public List<DanmakuBean> getDanmuBeanList() {
        return mDanmuBeanList;
    }

    public void setDanmuBeanList(List<DanmakuBean> danmuBeanList) {
        mDanmuBeanList = danmuBeanList;
    }
}

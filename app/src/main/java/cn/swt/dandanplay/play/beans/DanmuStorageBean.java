package cn.swt.dandanplay.play.beans;

import java.util.List;

import master.flame.danmaku.danmaku.model.BaseDanmaku;

/**
 * Title: DanmuStorageBean <br>
 * Description: 本地弹幕缓存数据<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-1-10 15:27
 * Created by Wentao.Shi.
 */
public class DanmuStorageBean {
    List<BaseDanmaku> mDanmuBeanList;

    public List<BaseDanmaku> getDanmuBeanList() {
        return mDanmuBeanList;
    }

    public void setDanmuBeanList(List<BaseDanmaku> danmuBeanList) {
        mDanmuBeanList = danmuBeanList;
    }
}

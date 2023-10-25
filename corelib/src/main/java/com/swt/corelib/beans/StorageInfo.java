package com.swt.corelib.beans;

/**
 * Title: StorageInfo <br>
 * Description: 系统挂载点<br>
 * Copyright (c) Hienao版权所有 2017 <br>
 * Created DateTime: 2017-3-3 12:30
 * Created by Wentao.Shi.
 */
public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemoveable;

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }
}

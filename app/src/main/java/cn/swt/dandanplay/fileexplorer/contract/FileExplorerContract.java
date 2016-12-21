package cn.swt.dandanplay.fileexplorer.contract;

import java.util.List;

import cn.swt.dandanplay.core.base.BasePresenter;
import cn.swt.dandanplay.core.base.BaseView;
import cn.swt.dandanplay.fileexplorer.beans.VideoFileInfo;

/**
 * Title: MainContract <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/17 10:43
 * Created by Wentao.Shi.
 */
public class FileExplorerContract {
    public interface View extends BaseView{
        void setData(List<VideoFileInfo>list);
        void getDataFromSQLite();
    }

    public interface Present extends BasePresenter {
        void getFileData(String contentpath);

    }
}

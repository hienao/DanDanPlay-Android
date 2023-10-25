package cn.swt.danmuplayer.fileexplorer.contract;

import java.util.List;

import cn.swt.danmuplayer.core.base.BasePresenter;
import cn.swt.danmuplayer.core.base.BaseView;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;

/**
 * Title: MainContract <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2016 <br>
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

package cn.swt.dandanplay.fileexplorer.component;

import cn.swt.dandanplay.fileexplorer.module.MainModule;
import cn.swt.dandanplay.fileexplorer.view.ContentsActivity;
import cn.swt.dandanplay.fileexplorer.view.FileExplorerActivity;
import dagger.Component;

/**
 * Title: MainComponent <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/17 11:03
 * Created by Wentao.Shi.
 */
@Component(modules = MainModule.class)
public interface MainComponent {
    void inject(ContentsActivity activity);
    void inject(FileExplorerActivity activity);
}

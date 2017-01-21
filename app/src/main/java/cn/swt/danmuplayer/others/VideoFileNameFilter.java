package cn.swt.danmuplayer.others;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Title: VideoFileNameFilter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 16:04
 * Created by Wentao.Shi.
 */
public class VideoFileNameFilter implements FilenameFilter {
    List<String> types;
    public VideoFileNameFilter(boolean usedefaulttype) {
        types = new ArrayList<String>();
        if (usedefaulttype){
            types.add(".asf");types.add(".avi");types.add(".mkv");types.add(".mp4");
            types.add(".mov");types.add(".flv");types.add(".mpeg");types.add(".mpg");
            types.add(".rmvb");types.add(".vob");types.add(".wmv");types.add(".3gp");
        }
    }

    public VideoFileNameFilter(List<String> types) {
        this.types = types;
    }

    @Override
    public boolean accept(File file, String s) {
        for (Iterator<String> iterator = types.iterator(); iterator.hasNext();) {
            String type = (String) iterator.next();
            if (s.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加后缀类型，如".avi"
     * @param type 后缀类型
     */
    public void addType(String type) {
        types.add(type);
    }

    /**
     * 添加后缀类型集合
     * @param types 后缀类型集合
     */
    public void addTypes(List<String> types) {
        types.addAll(types);
    }
}

package cn.swt.danmuplayer.fileexplorer.utils;

/**
 * Title: JudgeVideoUtil <br>
 * Description: 通过后缀名判断是否是视频文件<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-2-21 12:52
 * Created by Wentao.Shi.
 */
public class JudgeVideoUtil {
    static final String []videoSuffix={".mp4",".avi",".flv",".3gp",".mkv",".mpg",".rmvb",".mpeg"};

    /**
     * 判断是否为视频
     * @param filepath
     * @return
     */
    public static boolean isVideo(String filepath){
        boolean result=false;
        for (String vs:videoSuffix){
            if (filepath.endsWith(vs)){
                result=true;
                break;
            }
        }
        return result;
    }
}

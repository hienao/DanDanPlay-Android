package com.swt.corelib.utils;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/2
 *     desc  : 时间相关工具类
 * </pre>
 */
public class TimeUtils {
    public static String formatDuring(long mss) {
        boolean flag=false;
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        StringBuilder stringBuilder=new StringBuilder();
        if (days==0){
            stringBuilder.append("");
        }
        else{
            flag=true;
            stringBuilder.append(String.valueOf(days)+":");
        }
        if (hours==0){
            if (flag){
                stringBuilder.append("00:");
            }
            else {
                stringBuilder.append("");
            }
        }
        else{
            flag=true;
            stringBuilder.append(String.valueOf(hours)+":");
        }
        if (minutes==0){
            if (flag)
                stringBuilder.append("00:");
            else
                stringBuilder.append("");
        }
        else{
            flag=true;
            stringBuilder.append(String.valueOf(minutes)+":");
        }
        if (seconds==0){
            if (flag)
                stringBuilder.append("00");
            else
                stringBuilder.append("");
        }
        else
            stringBuilder.append(String.valueOf(seconds));
        return stringBuilder.toString();
    }
}
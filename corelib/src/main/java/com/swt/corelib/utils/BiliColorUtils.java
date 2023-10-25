package com.swt.corelib.utils;

import android.text.TextUtils;

/**
 * Title: BiliColorUtils <br>
 * Description: 颜色转换工具<br>
 * Copyright (c) Hienao版权所有 2016 <br>
 * Created DateTime: 2016/12/26 17:34
 * Created by Wentao.Shi.
 */
public class BiliColorUtils {
    /**
     * 将bilibili弹幕xml信息中的颜色字符串转化为dandanplay的颜色信息
     * @param bilicolor        10位16进制颜色字符串
     * @return 32位整形数的弹幕颜色，算法为 R*256*256 + G*256 + B。
     */
    public static int bilicolor2dandancolor(String bilicolor){
        if (bilicolor==null)
            return -1;
        if (TextUtils.isDigitsOnly(bilicolor)&&bilicolor.length()==8){
            int r=Integer.parseInt(bilicolor.substring(2,4),16);
            int g=Integer.parseInt(bilicolor.substring(4,6),16);
            int b=Integer.parseInt(bilicolor.substring(6,8),16);
            return r*256*256+g*256+b;
        }
        return -1;
    }
}

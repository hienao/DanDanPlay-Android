package cn.swt.danmuplayer.fileexplorer.utils;

import android.graphics.Rect;
import android.support.annotation.StringRes;

import com.nightonke.boommenu.BoomButtons.HamButton;

import cn.swt.danmuplayer.R;

/**
 * Title: BuilderManager <br>
 * Description: 菜单按钮的设置<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-2-10 12:50
 * Created by Wentao.Shi.
 */
public class BuilderManager {
    private static int[] imageResources = new int[]{
            R.drawable.ic_setting,
    };

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }


   public static HamButton.Builder getHamButtonBuilder(@StringRes int title, @StringRes int desc) {
        return new HamButton.Builder()
                .imagePadding(new Rect(30,30,30,30))
                .normalImageRes(getImageResource())
                .textSize(16)
                .normalTextRes(title)
                .subTextSize(13)
                .subNormalTextRes(desc);
    }

//    public static HamButton.Builder getHamButtonBuilderWithDifferentPieceColor() {
//        return new HamButton.Builder()
//                .normalImageRes(getImageResource())
//                .normalTextRes(R.string.text_ham_button_text_normal)
//                .subNormalTextRes(R.string.text_ham_button_sub_text_normal)
//                .pieceColor(Color.WHITE);
//    }

    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {
    }
}

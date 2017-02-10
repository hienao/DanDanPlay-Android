package cn.swt.danmuplayer.fileexplorer.utils;

import android.content.Context;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.swt.corelib.utils.ToastUtils;

import cn.swt.danmuplayer.R;

/**
 * Title: BmbUtil <br>
 * Description: BoomMenuButton工具<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017-2-10 15:47
 * Created by Wentao.Shi.
 */
public class BmbUtil {

    /**
     * 初始化菜单
     * @param bmb
     * @param context
     */
    public static void initBoomMenuButton(BoomMenuButton bmb, final Context context){
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                switch (index){
                    case 2:
                        ToastUtils.showShortToastSafe(context,"点击了设置按钮");
                        break;
                    default:
                        ToastUtils.showShortToastSafe(context,"点击了其他按钮");
                        break;
                }
            }

            @Override
            public void onBackgroundClick() {

            }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() {

            }

            @Override
            public void onBoomWillShow() {

            }

            @Override
            public void onBoomDidShow() {

            }
        });
    }

}

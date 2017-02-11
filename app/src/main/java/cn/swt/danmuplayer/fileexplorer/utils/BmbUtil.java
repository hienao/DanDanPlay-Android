package cn.swt.danmuplayer.fileexplorer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.swt.corelib.utils.ToastUtils;

import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.fileexplorer.view.ContentsActivity;
import cn.swt.danmuplayer.setting.SettingActivity;

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
     * @param activitycontext
     */
    public static void initBoomMenuButton(BoomMenuButton bmb, final Context activitycontext){
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting_danmuplayer,R.string.app_setting_danmuplayer_desc));
        bmb.addBuilder(BuilderManager.getHamButtonBuilder(R.string.app_setting,R.string.app_setting_desc));
        bmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                Intent intent;
                switch (index){
                    case 0:
                        Activity activity= (Activity) activitycontext;
                        if(!(activity  instanceof ContentsActivity)){
                            ToastUtils.showShortToastSafe(activitycontext,"点击了视频播放按钮");
                        }
                        break;
                    default:
                        intent=new Intent(activitycontext, SettingActivity.class);
                        activitycontext.startActivity(intent);
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

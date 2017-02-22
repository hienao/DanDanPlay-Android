package cn.swt.danmuplayer.setting;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.z.settingitemlib.SettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.core.base.BaseActivity;

import static cn.swt.danmuplayer.application.MyApplication.getSP;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.stool_toolbar_title)
    TextView mStoolToolbarTitle;
    @BindView(R.id.stool_toolbar)
    Toolbar mStoolToolbar;
    @BindView(R.id.set_scan_path)
    SettingItem mSetScanPath;
    @BindView(R.id.set_auto_play)
    SettingItem mSetAutoPlay;
    private boolean auto_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mSetScanPath.rightText.setText(getSP().getString("scan_path", "external"));
        auto_play=getSP().getBoolean("auto_play",true);
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_setting));
    }

    private void initListener() {
        mSetScanPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getString(R.string.app_setting_external).equals(mSetScanPath.rightText.getText().toString())) {
                    getSP().putString("scan_path", "internal");
                    mSetScanPath.rightText.setText(getResources().getString(R.string.app_setting_internal));
                } else {
                    getSP().putString("scan_path", "external");
                    mSetScanPath.rightText.setText(getResources().getString(R.string.app_setting_external));
                }
            }
        });
        mSetAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auto_play){
                    getSP().putBoolean("auto_play",false);
                    auto_play=false;
                    mSetAutoPlay.rightText.setText(getResources().getString(R.string.app_setting_no));
                }else {
                    getSP().putBoolean("auto_play",true);
                    auto_play=true;
                    mSetAutoPlay.rightText.setText(getResources().getString(R.string.app_setting_yes));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ("external".equals(getSP().getString("scan_path", "external"))) {
            mSetScanPath.rightText.setText(getResources().getString(R.string.app_setting_external));
        } else {
            mSetScanPath.rightText.setText(getResources().getString(R.string.app_setting_internal));
        }
        if (auto_play){
            mSetAutoPlay.rightText.setText(getResources().getString(R.string.app_setting_yes));
        }else {
            mSetAutoPlay.rightText.setText(getResources().getString(R.string.app_setting_no));
        }
    }
}

package cn.swt.danmuplayer.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.z.settingitemlib.SettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;

import static cn.swt.danmuplayer.application.MyApplication.getSP;

public class SettingFragment extends Fragment {

    @BindView(R.id.set_scan_path)
    SettingItem mSetScanPath;
    @BindView(R.id.set_auto_play)
    SettingItem mSetAutoPlay;
    private boolean auto_play;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        auto_play=getSP().getBoolean("auto_play",true);
    }

    private void initView() {
        mSetScanPath.rightText.setText(getSP().getString("scan_path", "external"));
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
    public void onStart() {
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

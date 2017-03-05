package cn.swt.danmuplayer.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.swt.corelib.permission.PermissionsActivity;
import com.swt.corelib.permission.PermissionsChecker;

import cn.swt.danmuplayer.R;

public class BaseActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 0; // 请求码
    private Toolbar  mToolbar;
    private TextView sTitleTV;
    // 所需的全部权限
    private String[] PERMISSIONS;
    public void setPERMISSIONS(String[] PERMISSIONS) {
        this.PERMISSIONS = PERMISSIONS;
    }
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionsChecker(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        sTitleTV = (TextView)findViewById(R.id.toolbar_title);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            mToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            mToolbar.setNavigationIcon(R.drawable.ic_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (PERMISSIONS != null && PERMISSIONS.length != 0) {
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
        }
    }

    /**
     * 设置后退按钮是否显示（默认显示）
     * @param state
     */
    public void setShowBackNavigationIcon(boolean state){
        if (state){
            mToolbar.setNavigationIcon(R.drawable.ic_back);
        }else {
            mToolbar.setNavigationIcon(null);
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    /**
     * 设置导航按钮
     * @param resId
     */
    public void setNavigationIcon(@DrawableRes int resId){
        mToolbar.setNavigationIcon(resId);
    }
    public void setCustomTitle(String title){
        sTitleTV.setText(title);
    }
    public Toolbar getToolbar(){
        return mToolbar;
    }
}

package cn.swt.danmuplayer.core.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cn.swt.danmuplayer.R;

public class BaseActivity extends AppCompatActivity {
    private Toolbar  mToolbar;
    private TextView sTitleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.stool_toolbar);
        sTitleTV = (TextView)findViewById(R.id.stool_toolbar_title);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            mToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.base_color_toolbar));
            mToolbar.setNavigationIcon(R.drawable.ic_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
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

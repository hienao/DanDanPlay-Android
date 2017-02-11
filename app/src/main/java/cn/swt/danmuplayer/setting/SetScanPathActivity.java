package cn.swt.danmuplayer.setting;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swt.corelib.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.base.BaseActivity;

public class SetScanPathActivity extends BaseActivity {

    @BindView(R.id.stool_toolbar_title)
    TextView mStoolToolbarTitle;
    @BindView(R.id.stool_toolbar)
    Toolbar mStoolToolbar;
    @BindView(R.id.text_scanpath_str)
    TextView mTextScanpathStr;
    @BindView(R.id.rv_file_contents)
    RecyclerView mRvFileContents;
    List<String>mList;
    private FolderRecycleViewAdapter mFolderRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_scan_path);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mList=new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(this);
        mRvFileContents.setLayoutManager(manager);
        mFolderRecycleViewAdapter = new FolderRecycleViewAdapter();
        mRvFileContents.setAdapter(mFolderRecycleViewAdapter);
    }

    /**
     *
     */
    private void getFolderList(String path) {
        mList.clear();
        if (!TextUtils.isEmpty(path)&&path.length()>1){
            mList.add("......");
        }
        List<File> fileList=FileUtils.listFilesInDir(path,false);
        if (fileList!=null){
            for (File f:fileList){
                if (FileUtils.isDir(f)){
                    mList.add(f.getName());
                }
            }
        }
        mFolderRecycleViewAdapter.notifyDataSetChanged();
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_setting_scan_path_select));
        mTextScanpathStr.setText(MyApplication.getSP().getString("scan_path","/"));
        getFolderList(mTextScanpathStr.getText().toString());
    }

    private void initListener() {
        mStoolToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_confirm:
                        MyApplication.getSP().putString("scan_path",mTextScanpathStr.getText().toString());
                        finish();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_confirm).setTitle(R.string.confirm);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_confirm, menu);
        return true;
    }

    class FolderRecycleViewAdapter extends RecyclerView.Adapter<FolderRecycleViewAdapter.ViewHolder>{


        @Override
        public FolderRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FolderRecycleViewAdapter.ViewHolder holder, final int position) {
            holder.textView.setText(mList.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("......".equals(mList.get(position))){
                        String parentpath=mTextScanpathStr.getText().toString().substring(0,mTextScanpathStr.getText().toString().length()-1);
                        parentpath=parentpath.substring(0,parentpath.lastIndexOf("/")+1);
                        mTextScanpathStr.setText(parentpath);
                    }else {
                        mTextScanpathStr.setText(mTextScanpathStr.getText().toString()+mList.get(position)+"/");
                    }
                    mList.clear();
                    getFolderList(mTextScanpathStr.getText().toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView ;
            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text_folder_name);
            }
        }
    }
}

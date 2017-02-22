package cn.swt.danmuplayer.setting;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.ProgressDialogUtils;
import com.swt.corelib.utils.TimeUtils;
import com.swt.corelib.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.base.BaseActivity;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.utils.JudgeVideoUtil;
import io.realm.Realm;

import static cn.swt.danmuplayer.fileexplorer.presenter.MainPresenter.getVideoThumbnail;
import static cn.swt.danmuplayer.fileexplorer.presenter.MainPresenter.restoreContentTable;

public class AddVideoFileManualActivity extends BaseActivity {

    @BindView(R.id.stool_toolbar_title)
    TextView mStoolToolbarTitle;
    @BindView(R.id.stool_toolbar)
    Toolbar mStoolToolbar;
    @BindView(R.id.text_scanpath_str)
    TextView mTextScanpathStr;
    @BindView(R.id.rv_file_contents)
    RecyclerView mRvFileContents;
    List<String> mList;
    private FolderRecycleViewAdapter mFolderRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_file_manual);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRvFileContents.setLayoutManager(manager);
        mFolderRecycleViewAdapter = new FolderRecycleViewAdapter();
        mRvFileContents.setAdapter(mFolderRecycleViewAdapter);
    }

    /**
     *
     */
    private void getFolderAndFileList(String path) {
        mList.clear();
        if (!TextUtils.isEmpty(path) && path.length() > 1) {
            mList.add("......");
        }
        List<File> fileList = FileUtils.listFilesInDir(path, false);
        if (fileList != null) {
            for (File f : fileList) {
                mList.add(f.getName());
            }
        }
        Collections.sort(mList);
        mFolderRecycleViewAdapter.notifyDataSetChanged();
    }

    private void initView() {
        setCustomTitle(getResources().getString(R.string.app_video_file_select));
        mTextScanpathStr.setText(MyApplication.getSP().getString("scan_path", Environment.getExternalStorageDirectory().toString() + "/"));
        getFolderAndFileList(mTextScanpathStr.getText().toString());
    }

    private void initListener() {

    }

    class FolderRecycleViewAdapter extends RecyclerView.Adapter<FolderRecycleViewAdapter.ViewHolder> {


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
                    Realm realm = MyApplication.getRealmInstance();
                    if ("......".equals(mList.get(position))) {
                        String parentpath = mTextScanpathStr.getText().toString().substring(0, mTextScanpathStr.getText().toString().length() - 1);
                        parentpath = parentpath.substring(0, parentpath.lastIndexOf("/") + 1);
                        mTextScanpathStr.setText(parentpath);
                        mList.clear();
                        getFolderAndFileList(parentpath);
                    } else {
                        String newfilepath = mTextScanpathStr.getText().toString() + mList.get(position);
                        if (FileUtils.isDir(newfilepath)) {
                            mTextScanpathStr.setText(newfilepath + "/");
                            mList.clear();
                            getFolderAndFileList(newfilepath + "/");
                        } else {
                            ProgressDialogUtils.showDialog(AddVideoFileManualActivity.this, getResources().getString(R.string.loading));
                            //检测文件在数据库中是否存在
                            VideoFileInfo videoFileInfoc = realm.where(VideoFileInfo.class).equalTo("videoPath", newfilepath).findFirst();
                            if (videoFileInfoc == null) {
                                if (!JudgeVideoUtil.isVideo(newfilepath)) {
                                    ToastUtils.showShortToastSafe(AddVideoFileManualActivity.this, "请选择视频文件！");
                                    ProgressDialogUtils.dismissDialog();
                                    return;
                                }
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(newfilepath);
                                String videoName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                                String videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                //检测文件是否后缀
                                VideoFileInfo videoFileInfo = new VideoFileInfo();
                                videoFileInfo.setVideoPath(newfilepath);
                                videoFileInfo.setVideoContentPath(newfilepath.substring(0, newfilepath.lastIndexOf("/") + 1));
                                if (TextUtils.isEmpty(videoName)) {
                                    videoFileInfo.setVideoName(videoName);
                                    videoFileInfo.setVideoNameWithoutSuffix("Unkonwn");
                                } else if (videoName.contains(".")) {
                                    videoFileInfo.setVideoName(videoName);
                                    videoFileInfo.setVideoNameWithoutSuffix(videoName.substring(0, videoName.lastIndexOf(".")));
                                } else {
                                    videoFileInfo.setVideoName(videoName);
                                    videoFileInfo.setVideoNameWithoutSuffix(videoName);
                                }
                                videoFileInfo.setCover(getVideoThumbnail(newfilepath));
                                try {
                                    videoFileInfo.setVideoLength(TimeUtils.formatDuring(Long.parseLong(videoDuration)));
                                } catch (NumberFormatException e) {
                                    videoFileInfo.setVideoLength("UnKnown");
                                }
                                realm.beginTransaction();
                                realm.copyToRealm(videoFileInfo);
                                realm.commitTransaction();
                                restoreContentTable();
                            } else {
                                ToastUtils.showShortToastSafe(AddVideoFileManualActivity.this, "数据库中已存在该数据，无法添加");
                            }
                            ProgressDialogUtils.dismissDialog();
                            finish();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text_folder_name);
            }
        }
    }
}

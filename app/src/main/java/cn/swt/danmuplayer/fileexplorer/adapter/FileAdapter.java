package cn.swt.danmuplayer.fileexplorer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swt.corelib.utils.ConvertUtils;
import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.NetworkUtils;

import java.util.List;

import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileArgInfo;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.presenter.ContentPresenter;
import cn.swt.danmuplayer.fileexplorer.view.EpisodeIdMatchActivity;
import cn.swt.danmuplayer.play.view.VideoViewActivity;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Title: ContentAdapter <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 19:56
 * Created by Wentao.Shi.
 */
public class FileAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    public List<VideoFileInfo> list;
    private Context mContext;
    private boolean mNetworkMode;

    public FileAdapter(Context context, List<VideoFileInfo> list) {
        this.list = list;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 设置网络模式
     *
     * @param networkMode
     */
    public void setNetworkMode(boolean networkMode) {
        mNetworkMode = networkMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_file_info, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text_name.setText(list.get(position).getVideoNameWithoutSuffix());
        viewHolder.text_time.setText(list.get(position).getVideoLength());
        try {
            viewHolder.iv_cover.setImageBitmap(ConvertUtils.base64ToBitmap(list.get(position).getCover()));
        } catch (Exception e) {
            //获取缩略图失败
        }
        Realm realm = MyApplication.getRealmInstance();
        VideoFileArgInfo videoFileArgInfo = realm.where(VideoFileArgInfo.class).equalTo("videoPath", list.get(position).getVideoPath()).findFirst();
        if (videoFileArgInfo != null) {
            viewHolder.ic_local_danmu_exist.setImageDrawable(videoFileArgInfo.isHaveLocalDanmu() ? ContextCompat.getDrawable(mContext, R.drawable.ic_true) : ContextCompat.getDrawable(mContext, R.drawable.ic_false));
        }
        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNetworkMode && NetworkUtils.isConnected(mContext)) {
                    mContext.startActivity(new Intent(mContext, EpisodeIdMatchActivity.class)
                            .putExtra("path", list.get(position).getVideoPath())
                            .putExtra("title", list.get(position).getVideoNameWithoutSuffix()));
                } else {
                    mContext.startActivity(new Intent(mContext, VideoViewActivity.class)
                            .putExtra("path", list.get(position).getVideoPath())
                            .putExtra("file_title", list.get(position).getVideoNameWithoutSuffix())
                            .putExtra("isoffline", true));
                }

            }
        });
        viewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenuDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_name, text_time;
        private ImageView iv_cover, ic_local_danmu_exist;
        private LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            text_name = (TextView) itemView.findViewById(R.id.text_file_name);
            text_time = (TextView) itemView.findViewById(R.id.text_viedo_time);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_video_cover);
            mLayout = (LinearLayout) itemView.findViewById(R.id.llayout_file);
            ic_local_danmu_exist = (ImageView) itemView.findViewById(R.id.iv_local_danmu_exist);
        }
    }

    private void showMenuDialog(final int position) {
        final String items[] = {mContext.getResources().getString(R.string.only_del_file), mContext.getResources().getString(R.string.only_del_danmu),
                mContext.getResources().getString(R.string.del_file_danmu)};
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  //先得到构造器
        builder.setTitle(list.get(position).getVideoNameWithoutSuffix()); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filePath = list.get(position).getVideoPath();
                String ddxml = filePath.substring(0, filePath.lastIndexOf(".")) + "dd.xml";
                String bilixml = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";
                switch (which) {
                    case 0:
                        deletevideofile(filePath, position);
                        break;
                    case 1:
                        deletedanmufile(filePath);
                        break;
                    case 2:
                        deletevideofile(filePath, position);
                        deletedanmufile(filePath);
                        break;
                }
                notifyDataSetChanged();
                dialog.dismiss();
                if (list == null || list.size() == 0) {
                    ((Activity) mContext).finish();
                }
            }
        });
        builder.create().show();
    }

    private void deletevideofile(String filePath, int position) {
        if (FileUtils.isFileExists(filePath)) {
            FileUtils.deleteFile(filePath);
        }
        Realm realm = MyApplication.getRealmInstance();
        RealmResults<VideoFileInfo> videoFileInfoc = realm.where(VideoFileInfo.class).equalTo("videoPath", filePath).findAll();
        realm.beginTransaction();
        if (videoFileInfoc != null&&videoFileInfoc.size()>0) {
            videoFileInfoc.deleteAllFromRealm();
        }
        realm.commitTransaction();
        videoFileInfoc = realm.where(VideoFileInfo.class).equalTo("videoPath", filePath).findAll();
        VideoFileArgInfo videoFileArgInfo = realm.where(VideoFileArgInfo.class).equalTo("videoPath", filePath).findFirst();
        realm.beginTransaction();
        if (videoFileArgInfo != null) {
            videoFileArgInfo.deleteFromRealm();
        }
        realm.commitTransaction();
        ContentPresenter.restoreContentTable();
        list.remove(position);
    }

    private void deletedanmufile(String videoFilePath) {
        String ddxml = videoFilePath.substring(0, videoFilePath.lastIndexOf(".")) + "dd.xml";
        String bilixml = videoFilePath.substring(0, videoFilePath.lastIndexOf(".")) + ".xml";
        if (FileUtils.isFileExists(ddxml)) {
            FileUtils.deleteFile(ddxml);
        }
        if (FileUtils.isFileExists(bilixml)) {
            FileUtils.deleteFile(bilixml);
        }
        Realm realm = MyApplication.getRealmInstance();
        VideoFileArgInfo videoFileArgInfo = realm.where(VideoFileArgInfo.class).equalTo("videoPath", videoFilePath).findFirst();
        realm.beginTransaction();
        if (videoFileArgInfo != null) {
            videoFileArgInfo.setHaveLocalDanmu(false);
        }
        realm.commitTransaction();
    }
}

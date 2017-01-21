package cn.swt.danmuplayer.fileexplorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swt.corelib.utils.ConvertUtils;
import com.swt.corelib.utils.NetworkUtils;

import java.util.List;

import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileInfo;
import cn.swt.danmuplayer.fileexplorer.view.EpisodeIdMatchActivity;
import cn.swt.danmuplayer.play.view.VideoViewActivity;

/**
 * Title: ContentAdapter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 19:56
 * Created by Wentao.Shi.
 */
public class FileAdapter extends RecyclerView.Adapter{

    private LayoutInflater inflater;
    public List<VideoFileInfo> list;
    private Context mContext;
    private boolean mNetworkMode;

    public FileAdapter(Context context, List<VideoFileInfo> list) {
        this.list = list;
        mContext=context;
        inflater=LayoutInflater.from(context);
    }

    /**
     * 设置网络模式
     * @param networkMode
     */
    public void setNetworkMode(boolean networkMode){
        mNetworkMode=networkMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_file_info,parent,false));
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
        viewHolder.ic_local_danmu_exist.setImageDrawable(list.get(position).isHaveLocalDanmu()? ContextCompat.getDrawable(mContext,R.drawable.ic_true): ContextCompat.getDrawable(mContext,R.drawable.ic_false));
        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNetworkMode&&NetworkUtils.isConnected(mContext)){
                    mContext.startActivity(new Intent(mContext, EpisodeIdMatchActivity.class)
                            .putExtra("path",list.get(position).getVideoPath())
                            .putExtra("title",list.get(position).getVideoNameWithoutSuffix()));
                }else{
                    mContext.startActivity(new Intent(mContext, VideoViewActivity.class)
                            .putExtra("path",list.get(position).getVideoPath())
                            .putExtra("file_title",list.get(position).getVideoNameWithoutSuffix())
                            .putExtra("isoffline",true));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private class ViewHolder extends RecyclerView.ViewHolder{

        private TextView text_name,text_time;
        private ImageView iv_cover,ic_local_danmu_exist;
        private LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            text_name = (TextView) itemView.findViewById(R.id.text_file_name);
            text_time = (TextView) itemView.findViewById(R.id.text_viedo_time);
            iv_cover= (ImageView) itemView.findViewById(R.id.iv_video_cover);
            mLayout = (LinearLayout) itemView.findViewById(R.id.llayout_file);
            ic_local_danmu_exist= (ImageView) itemView.findViewById(R.id.iv_local_danmu_exist);
        }
    }
}

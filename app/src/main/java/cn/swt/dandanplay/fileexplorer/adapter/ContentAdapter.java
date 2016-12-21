package cn.swt.dandanplay.fileexplorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.swt.dandanplay.R;
import cn.swt.dandanplay.fileexplorer.beans.ContentInfo;
import cn.swt.dandanplay.fileexplorer.view.FileExplorerActivity;

/**
 * Title: ContentAdapter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/22 0022 19:56
 * Created by Wentao.Shi.
 */
public class ContentAdapter extends RecyclerView.Adapter{

    private LayoutInflater inflater;
    public List<ContentInfo> list;
    private Context mContext;

    public ContentAdapter(Context context,List<ContentInfo> list) {
        this.list = list;
        mContext=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_content_info,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text_name.setText(list.get(position).getContentName());
        viewHolder.text_count.setText(list.get(position).getCount()+"   "+mContext.getResources().getString(R.string.video));
        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, FileExplorerActivity.class)
                        .putExtra("contentpath",list.get(position).getContentPath()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private class ViewHolder extends RecyclerView.ViewHolder{

        private TextView text_name,text_count;
        private LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            text_name = (TextView) itemView.findViewById(R.id.text_content_name);
            text_count = (TextView) itemView.findViewById(R.id.text_content_count);
            mLayout= (LinearLayout) itemView.findViewById(R.id.llayout_content);
        }
    }
}

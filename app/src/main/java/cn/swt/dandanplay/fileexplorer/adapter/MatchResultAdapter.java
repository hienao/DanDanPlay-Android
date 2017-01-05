package cn.swt.dandanplay.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cn.swt.dandanplay.core.http.beans.SearchAllResponse;

/**
 * Title: MatchResultAdapter <br>
 * Description: 匹配结果列表adapter<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017/1/5 0005 19:05
 * Created by Wentao.Shi.
 */
public class MatchResultAdapter extends RecyclerView.Adapter {
    private LayoutInflater                       inflater;
    public  List<SearchAllResponse.AnimesBean> list;
    private Context                              mContext;

    public MatchResultAdapter(Context context, List<SearchAllResponse.AnimesBean> list) {
        this.list = list;
        mContext=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

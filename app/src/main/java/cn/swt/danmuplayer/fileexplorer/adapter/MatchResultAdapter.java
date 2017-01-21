package cn.swt.danmuplayer.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swt.corelib.utils.ProgressDialogUtils;

import java.util.List;

import cn.swt.danmuplayer.R;
import cn.swt.danmuplayer.fileexplorer.beans.SearchResultInfo;
import cn.swt.danmuplayer.fileexplorer.utils.DanmuUtils;

/**
 * Title: MatchResultAdapter <br>
 * Description: 匹配结果列表adapter<br>
 * Copyright (c) 传化物流版权所有 2017 <br>
 * Created DateTime: 2017/1/5 0005 19:05
 * Created by Wentao.Shi.
 */
public class MatchResultAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = 0, TYPE_CONTENT = 1;
    private LayoutInflater inflater;
    public List<SearchResultInfo> list;
    private Context mContext;
    private String videoPath, videoTitle;

    public MatchResultAdapter(Context context, List<SearchResultInfo> list, String videoPath, String videoTitle) {
        this.list = list;
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.videoPath = videoPath;
        this.videoTitle = videoTitle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_episode_id_header, parent, false);

            return new HeaderHolder(view);

        } else if (viewType == TYPE_CONTENT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_episode_id_info, parent, false);
            return new InfoHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).header.setText(list.get(position).getMainTitle());
            String type = "";
            switch (list.get(position).getType()) {
                case 1:
                    type = "TV动画";
                    break;
                case 2:
                    type = "TV动画特别放送";
                    break;
                case 3:
                    type = "OVA";
                    break;
                case 4:
                    type = "剧场版";
                    break;
                case 5:
                    type = "音乐视频（MV）";
                    break;
                case 6:
                    type = "网络放送";
                    break;
                case 7:
                    type = "其他分类";
                    break;
                case 10:
                    type = "电影";
                    break;
                case 20:
                    type = "电视剧或国产动画";
                    break;
                default:
                    type = "未知";
                    break;
            }
            ((HeaderHolder) holder).type.setText(type);
        } else if (holder instanceof InfoHolder) {
            ((InfoHolder) holder).title.setText(list.get(position).getTitle());
            ((InfoHolder) holder).title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialogUtils.showDialog(mContext,mContext.getResources().getString(R.string.danmu_loading));
                    DanmuUtils danmuUtils =new DanmuUtils(mContext,videoPath,videoTitle,
                            list.get(position).getMainTitle() + " " + list.get(position).getTitle(),list.get(position).getId());
                    danmuUtils.getDanmuListByEspoisedId();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isHeader())
            return TYPE_HEADER;
        else
            return TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header, type;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.text_search_episode_id_header);
            type = (TextView) itemView.findViewById(R.id.text_search_episode_id_type);
        }
    }

    public class InfoHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public InfoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_search_episode_title);
        }
    }
}

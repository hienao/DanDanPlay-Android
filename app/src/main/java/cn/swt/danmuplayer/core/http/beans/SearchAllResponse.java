package cn.swt.danmuplayer.core.http.beans;

import java.util.List;

/**
 * Title: SearchAllResponse <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 21:06
 * Created by Wentao.Shi.
 */
public class SearchAllResponse {

    /**
     * HasMore : true       当结果集过大时，HasMore属性为true，这时客户端应该提示用户填写更详细的信息以缩小搜索范围。
     * Animes : [{"Title":"sample string 1","Type":2,"Episodes":[{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"}]},{"Title":"sample string 1","Type":2,"Episodes":[{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"}]},{"Title":"sample string 1","Type":2,"Episodes":[{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"}]}]
     * Type 属性的含义：
     * 1 - TV动画
     * 2 - TV动画特别放送
     * 3 - OVA
     * 4 - 剧场版
     * 5 - 音乐视频（MV）
     * 6 - 网络放送
     * 7 - 其他分类
     * 10 - 三次元电影
     * 20 - 三次元电视剧或国产动画
     * 99- 未知（尚未分类）
     */

    private boolean HasMore;
    private List<AnimesBean> Animes;

    public boolean isHasMore() {
        return HasMore;
    }

    public void setHasMore(boolean HasMore) {
        this.HasMore = HasMore;
    }

    public List<AnimesBean> getAnimes() {
        return Animes;
    }

    public void setAnimes(List<AnimesBean> Animes) {
        this.Animes = Animes;
    }

    public static class AnimesBean {
        /**
         * Title : sample string 1
         * Type : 2
         * Episodes : [{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"},{"Id":1,"Title":"sample string 2"}]
         */

        private String Title;
        private int Type;
        private List<EpisodesBean> Episodes;

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public int getType() {
            return Type;
        }

        public void setType(int Type) {
            this.Type = Type;
        }

        public List<EpisodesBean> getEpisodes() {
            return Episodes;
        }

        public void setEpisodes(List<EpisodesBean> Episodes) {
            this.Episodes = Episodes;
        }

        public static class EpisodesBean {
            /**
             * Id : 1
             * Title : sample string 2
             */

            private int Id;
            private String Title;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getTitle() {
                return Title;
            }

            public void setTitle(String Title) {
                this.Title = Title;
            }
        }
    }
}

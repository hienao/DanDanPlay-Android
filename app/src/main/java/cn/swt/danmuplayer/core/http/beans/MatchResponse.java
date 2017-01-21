package cn.swt.danmuplayer.core.http.beans;

/**
 * Title: MatchResponse <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:57
 * Created by Wentao.Shi.
 */
/**
 * EpisodeId : 1                    节目编号
 * AnimeTitle : sample string 2     动画标题
 * EpisodeTitle : sample string 3   节目标题
 * Type : 4                         类型
 * Shift : 5.1                      偏移
 */

import java.util.List;

public class MatchResponse {

    private List<MatchesBean> Matches;

    public List<MatchesBean> getMatches() {
        return Matches;
    }

    public void setMatches(List<MatchesBean> Matches) {
        this.Matches = Matches;
    }

    public static class MatchesBean {
        /**
         * EpisodeId : 119040008
         * AnimeTitle : 少女编号
         * EpisodeTitle : 第8话 ねぼすけ千歳と湯煙旅情
         * Type : 1
         * Shift : 0
         */

        private int EpisodeId;
        private String AnimeTitle;
        private String EpisodeTitle;
        private int    Type;
        private int    Shift;

        public int getEpisodeId() {
            return EpisodeId;
        }

        public void setEpisodeId(int EpisodeId) {
            this.EpisodeId = EpisodeId;
        }

        public String getAnimeTitle() {
            return AnimeTitle;
        }

        public void setAnimeTitle(String AnimeTitle) {
            this.AnimeTitle = AnimeTitle;
        }

        public String getEpisodeTitle() {
            return EpisodeTitle;
        }

        public void setEpisodeTitle(String EpisodeTitle) {
            this.EpisodeTitle = EpisodeTitle;
        }

        public int getType() {
            return Type;
        }

        public void setType(int Type) {
            this.Type = Type;
        }

        public int getShift() {
            return Shift;
        }

        public void setShift(int Shift) {
            this.Shift = Shift;
        }
    }
}

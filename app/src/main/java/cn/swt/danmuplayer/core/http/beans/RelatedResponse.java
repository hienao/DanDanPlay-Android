package cn.swt.danmuplayer.core.http.beans;

import java.util.List;

/**
 * Title: RelatedResponse <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:25
 * Created by Wentao.Shi.
 */
public class RelatedResponse {

    private List<RelatedsBean> Relateds;

    public List<RelatedsBean> getRelateds() {
        return Relateds;
    }

    public void setRelateds(List<RelatedsBean> Relateds) {
        this.Relateds = Relateds;
    }

    public static class RelatedsBean {
        /**
         * Provider : 弹幕提供者
         * Url : 源地址
         * Shift : 弹幕偏移量
         */

        private String Provider;
        private String Url;
        private double Shift;

        public String getProvider() {
            return Provider;
        }

        public void setProvider(String Provider) {
            this.Provider = Provider;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }

        public double getShift() {
            return Shift;
        }

        public void setShift(double Shift) {
            this.Shift = Shift;
        }
    }
}

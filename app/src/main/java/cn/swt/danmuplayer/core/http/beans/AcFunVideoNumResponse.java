package cn.swt.danmuplayer.core.http.beans;

import java.util.List;

/**
 * Title: AcFunVideoNumResponse <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2017 <br>
 * Created DateTime: 2017/1/7 0007 15:22
 * Created by Wentao.Shi.
 */
public class AcFunVideoNumResponse {

    /**
     * code : 200
     * data : {"channelId":106,"contentId":3381864,"cover":"http://imgs.aixifan.com/content/2017_01_05/1483586266.gif","description":"Acfun3000基佬聚集地群号： 235261238<br/>（什么，居然有女AC饭！）\r<br/>视频制作：晨上初雨\r<br/>配音CV微博：muriko芜马甲太多 http://weibo.com/muriko \r<br/>背景BUG：China-X 徐梦圆","display":0,"isArticle":0,"isComment":true,"isRecommend":0,"owner":{"avatar":"http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201610/20112219s8ylbmil.jpg","id":973769,"name":"晨上初雨","verified":0,"verifiedText":""},"parentChannelId":1,"releaseDate":1483587979000,"status":2,"title":"国漫崛起 2016年所有国漫总结点评，亮瞎你的狗眼","topLevel":0,"updatedAt":1483772788000,"videoCount":1,"videos":[{"allowDanmaku":0,"commentId":4714169,"danmakuId":4714169,"sort":0,"sourceId":"4685386","sourceType":"zhuzhan","startTime":0,"time":517,"title":"Part1","url":"","videoId":4714169,"visibleLevel":-1}],"viewOnly":0,"visit":{"comments":218,"danmakuSize":281,"goldBanana":976,"score":0,"stows":566,"ups":0,"views":75576}}
     * message : OK
     */

    private int code;
    private DataBean data;
    private String   message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * channelId : 106
         * contentId : 3381864
         * cover : http://imgs.aixifan.com/content/2017_01_05/1483586266.gif
         * description : Acfun3000基佬聚集地群号： 235261238<br/>（什么，居然有女AC饭！）<br/>视频制作：晨上初雨<br/>配音CV微博：muriko芜马甲太多 http://weibo.com/muriko <br/>背景BUG：China-X 徐梦圆
         * display : 0
         * isArticle : 0
         * isComment : true
         * isRecommend : 0
         * owner : {"avatar":"http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201610/20112219s8ylbmil.jpg","id":973769,"name":"晨上初雨","verified":0,"verifiedText":""}
         * parentChannelId : 1
         * releaseDate : 1483587979000
         * status : 2
         * title : 国漫崛起 2016年所有国漫总结点评，亮瞎你的狗眼
         * topLevel : 0
         * updatedAt : 1483772788000
         * videoCount : 1
         * videos : [{"allowDanmaku":0,"commentId":4714169,"danmakuId":4714169,"sort":0,"sourceId":"4685386","sourceType":"zhuzhan","startTime":0,"time":517,"title":"Part1","url":"","videoId":4714169,"visibleLevel":-1}]
         * viewOnly : 0
         * visit : {"comments":218,"danmakuSize":281,"goldBanana":976,"score":0,"stows":566,"ups":0,"views":75576}
         */

        private int channelId;
        private int              contentId;
        private String           cover;
        private String           description;
        private int              display;
        private int              isArticle;
        private boolean          isComment;
        private int              isRecommend;
        private OwnerBean        owner;
        private int              parentChannelId;
        private long             releaseDate;
        private int              status;
        private String           title;
        private int              topLevel;
        private long             updatedAt;
        private int              videoCount;
        private int              viewOnly;
        private VisitBean        visit;
        private List<VideosBean> videos;

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public int getContentId() {
            return contentId;
        }

        public void setContentId(int contentId) {
            this.contentId = contentId;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDisplay() {
            return display;
        }

        public void setDisplay(int display) {
            this.display = display;
        }

        public int getIsArticle() {
            return isArticle;
        }

        public void setIsArticle(int isArticle) {
            this.isArticle = isArticle;
        }

        public boolean isIsComment() {
            return isComment;
        }

        public void setIsComment(boolean isComment) {
            this.isComment = isComment;
        }

        public int getIsRecommend() {
            return isRecommend;
        }

        public void setIsRecommend(int isRecommend) {
            this.isRecommend = isRecommend;
        }

        public OwnerBean getOwner() {
            return owner;
        }

        public void setOwner(OwnerBean owner) {
            this.owner = owner;
        }

        public int getParentChannelId() {
            return parentChannelId;
        }

        public void setParentChannelId(int parentChannelId) {
            this.parentChannelId = parentChannelId;
        }

        public long getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(long releaseDate) {
            this.releaseDate = releaseDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getTopLevel() {
            return topLevel;
        }

        public void setTopLevel(int topLevel) {
            this.topLevel = topLevel;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getVideoCount() {
            return videoCount;
        }

        public void setVideoCount(int videoCount) {
            this.videoCount = videoCount;
        }

        public int getViewOnly() {
            return viewOnly;
        }

        public void setViewOnly(int viewOnly) {
            this.viewOnly = viewOnly;
        }

        public VisitBean getVisit() {
            return visit;
        }

        public void setVisit(VisitBean visit) {
            this.visit = visit;
        }

        public List<VideosBean> getVideos() {
            return videos;
        }

        public void setVideos(List<VideosBean> videos) {
            this.videos = videos;
        }

        public static class OwnerBean {
            /**
             * avatar : http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201610/20112219s8ylbmil.jpg
             * id : 973769
             * name : 晨上初雨
             * verified : 0
             * verifiedText :
             */

            private String avatar;
            private int    id;
            private String name;
            private int    verified;
            private String verifiedText;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getVerified() {
                return verified;
            }

            public void setVerified(int verified) {
                this.verified = verified;
            }

            public String getVerifiedText() {
                return verifiedText;
            }

            public void setVerifiedText(String verifiedText) {
                this.verifiedText = verifiedText;
            }
        }

        public static class VisitBean {
            /**
             * comments : 218
             * danmakuSize : 281
             * goldBanana : 976
             * score : 0
             * stows : 566
             * ups : 0
             * views : 75576
             */

            private int comments;
            private int danmakuSize;
            private int goldBanana;
            private int score;
            private int stows;
            private int ups;
            private int views;

            public int getComments() {
                return comments;
            }

            public void setComments(int comments) {
                this.comments = comments;
            }

            public int getDanmakuSize() {
                return danmakuSize;
            }

            public void setDanmakuSize(int danmakuSize) {
                this.danmakuSize = danmakuSize;
            }

            public int getGoldBanana() {
                return goldBanana;
            }

            public void setGoldBanana(int goldBanana) {
                this.goldBanana = goldBanana;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public int getStows() {
                return stows;
            }

            public void setStows(int stows) {
                this.stows = stows;
            }

            public int getUps() {
                return ups;
            }

            public void setUps(int ups) {
                this.ups = ups;
            }

            public int getViews() {
                return views;
            }

            public void setViews(int views) {
                this.views = views;
            }
        }

        public static class VideosBean {
            /**
             * allowDanmaku : 0
             * commentId : 4714169
             * danmakuId : 4714169
             * sort : 0
             * sourceId : 4685386
             * sourceType : zhuzhan
             * startTime : 0
             * time : 517
             * title : Part1
             * url :
             * videoId : 4714169
             * visibleLevel : -1
             */

            private int allowDanmaku;
            private int    commentId;
            private int    danmakuId;
            private int    sort;
            private String sourceId;
            private String sourceType;
            private int    startTime;
            private int    time;
            private String title;
            private String url;
            private int    videoId;
            private int    visibleLevel;

            public int getAllowDanmaku() {
                return allowDanmaku;
            }

            public void setAllowDanmaku(int allowDanmaku) {
                this.allowDanmaku = allowDanmaku;
            }

            public int getCommentId() {
                return commentId;
            }

            public void setCommentId(int commentId) {
                this.commentId = commentId;
            }

            public int getDanmakuId() {
                return danmakuId;
            }

            public void setDanmakuId(int danmakuId) {
                this.danmakuId = danmakuId;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public String getSourceId() {
                return sourceId;
            }

            public void setSourceId(String sourceId) {
                this.sourceId = sourceId;
            }

            public String getSourceType() {
                return sourceType;
            }

            public void setSourceType(String sourceType) {
                this.sourceType = sourceType;
            }

            public int getStartTime() {
                return startTime;
            }

            public void setStartTime(int startTime) {
                this.startTime = startTime;
            }

            public int getTime() {
                return time;
            }

            public void setTime(int time) {
                this.time = time;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getVideoId() {
                return videoId;
            }

            public void setVideoId(int videoId) {
                this.videoId = videoId;
            }

            public int getVisibleLevel() {
                return visibleLevel;
            }

            public void setVisibleLevel(int visibleLevel) {
                this.visibleLevel = visibleLevel;
            }
        }
    }
}

package cn.swt.danmuplayer.core.http;

import cn.swt.danmuplayer.core.http.beans.AcFunVideoNumResponse;
import cn.swt.danmuplayer.core.http.beans.CidResponse;
import cn.swt.danmuplayer.core.http.beans.CommentResponse;
import cn.swt.danmuplayer.core.http.beans.MatchResponse;
import cn.swt.danmuplayer.core.http.beans.RelatedResponse;
import cn.swt.danmuplayer.core.http.beans.SearchAllResponse;
import cn.swt.danmuplayer.core.http.beans.SendCommentResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Title: APIService <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/11/23 0023 20:21
 * Created by Wentao.Shi.
 */
public interface APIService {

    /**
     * 获取指定节目编号对应的所有第三方弹幕源信息
     * @param episodeId              节目编号
     * @return
     */
    @GET("api/v1/related/{episodeId}")
    Call<RelatedResponse> getCommentSource(@Path("episodeId") String episodeId);

    /**
     * 获取指定弹幕库（节目编号）的所有弹幕
     * @param episodeId         弹幕库ID（节目编号）
     * @param from              起始弹幕编号，忽略此编号以前的弹幕。默认值为0
     * @return
     */
    @GET("api/v1/comment/{episodeId}")
    Call<CommentResponse> getComment(@Path("episodeId") String episodeId,@Query("from") String from);

    /**
     * 向指定的非流媒体节目发送弹幕
     * @param episodeId             节目编号
     * @param clientId              客户端ID
     * @param encryptedText         加密后的json字符串
     * @return
     */
    @PUT("api/v1/comment/{episodeId}")
    @Headers("Content-Type: application/json") //如果无法获取结果把这条去掉试试
    Call<SendCommentResponse> sendComment(@Path("episodeId") String episodeId, @Query("clientId") String clientId,
                                          @Query("encryptedText") String encryptedText);

    /**
     * 向指定的流媒体频道或私有频道发送弹幕
     * @param episodeId                 频道编号
     * @param token                     需要提交的弹幕信息  为CommentRequestBean对应的json字符串
     * @param sendCommentItem           向私有弹幕库发送弹幕时需要验证的密码
     * @return
     */
    @POST("api/v1/comment/{episodeId}?token={token}")
    Call<SendCommentResponse> sendCommentToStreamMedia(@Path("episodeId") String episodeId, @Path("token") String token,
                                          @Query("sendCommentItem") String sendCommentItem);

    /**
     * 使用指定的文件名、Hash、文件长度信息寻找文件可能对应的节目信息
     * @param fileName              视频文件名，不包含文件夹名称和扩展名，特殊字符需进行转义
     * @param hash                  文件前16MB(16x1024x1024Byte)数据的32位MD5结果，不区分大小写
     * @param length                文件总长度，单位为Byte
     * @param duration              [可选参数]32位整数的视频时长，单位为秒。默认为0
     * @param force                 [可选参数]强制使用一个模式进行匹配。默认为0（不启用），设置为1将使用文件名（不使用MD5）匹配，设置为2将使用MD5（不使用文件名）匹配
     * @return
     */
    @GET("api/v1/match")
    Call<MatchResponse> matchEpisodeId(@Query("fileName") String fileName, @Query("hash") String hash, @Query("length") String length,
                                       @Query("duration") String duration, @Query("force") String force);

    /**
     * 根据关键词搜索所有匹配的剧集信息。当自动匹配失败或结果不理想时可以调用此接口，让用户手动搜索并选择关联信息
     * @param episodeId     [可选参数]节目子标题，默认为空。
                            当此值为纯数字时，将过滤搜索结果，仅保留指定集数的条目。
                            当此值为“movie”时，将过滤搜索结果，仅保留剧场版条目。
                            当此值为其他字符串时，将过滤搜索结果，仅保留子标题包含指定文字的条目，不建议使用。

     * @param anime         动画标题，支持通过中文、日语（含罗马音）、英语搜索，至少为2个字符
     *
     * ps:                  参数可以包含空格，但空格将作为查询字符串的一部分而不是传统的“OR”查询。
                            如果未填写episode参数，anime参数中包含空格，且空格后为数字（如“EVA 10”），此数字将被认定为episode参数。
                            如果参数中包含特殊字符则需要经过Url编码后才能传递。
     * @return
     */
    @GET("api/v1/searchall/{anime}/{episodeId}")
    Call<SearchAllResponse> searchALLEpisodeId(@Path("anime") String anime,@Path("episodeId") String episodeId);

    /**
     * 获取bilibili的视频cid
     * 基础地址为https://biliproxy.chinacloudsites.cn/
     * @param avnum av号(不带av)
     * @param page  page页码
     * @return
     */
    @GET("av/{avnum}/{page}")
    Call<CidResponse> getBiliBiliCid(@Path("avnum") String avnum,@Path("page") String page);

    /**
     * 获取acfun的videoId
     * @param ac_num        网址中以ac开头的数字
     * @return
     */
    @GET("{ac_num}")
    Call<AcFunVideoNumResponse>getAcFunAcid(@Path("ac_num")String ac_num);

    /**
     * @param video_url
     * @return
     */
    @GET("api/v1/extcomment")
    Call<CommentResponse> getOtherCommentByVideoUrl(@Query("url") String video_url);

}

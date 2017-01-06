package cn.swt.dandanplay.play.presenter;

import android.text.TextUtils;

import com.swt.corelib.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.swt.dandanplay.core.http.APIService;
import cn.swt.dandanplay.core.http.HttpConstant;
import cn.swt.dandanplay.core.http.RetrofitManager;
import cn.swt.dandanplay.core.http.SAXContentHandler;
import cn.swt.dandanplay.core.http.beans.CidResponse;
import cn.swt.dandanplay.core.http.beans.CommentResponse;
import cn.swt.dandanplay.core.http.beans.RelatedResponse;
import cn.swt.dandanplay.play.contract.VideoViewContract;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Title: VideoViewPresenter <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/10/29 0029 9:44
 * Created by Wentao.Shi.
 */
public class VideoViewPresenter implements VideoViewContract.Present {
    private VideoViewContract.View mView;

    @Inject
    public VideoViewPresenter(VideoViewContract.View view) {
        mView = view;
    }

    @Override
    public void getComment(String episodeId, String from) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getComment(episodeId, from), new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                mView.gotComment(commentResponse);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "commentResponse Error", t);
            }
        });
    }

    @Override
    public void getCommentSource(String episodeId) {
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getCommentSource(episodeId), new Callback<RelatedResponse>() {
            @Override
            public void onResponse(Call<RelatedResponse> call, Response<RelatedResponse> response) {
                RelatedResponse relatedResponse = response.body();
                getOtherComment(relatedResponse.getRelateds());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.e("VideoViewPresenter", "commentResponse Error", t);
            }
        });

    }

    /**
     * 获取第三方弹幕源弹幕
     *
     * @param relatedsBeanList
     */
    private void getOtherComment(List<RelatedResponse.RelatedsBean> relatedsBeanList) {
        if (relatedsBeanList != null && relatedsBeanList.size() != 0) {
            for (RelatedResponse.RelatedsBean relatedsBean : relatedsBeanList) {
                if (relatedsBean.getProvider().contains("BiliBili")) {
                    //按bilibili解析弹幕
                    String biliVideoUrl = relatedsBean.getUrl();
                    if (TextUtils.isEmpty(biliVideoUrl))
                        return;
                    String avnum = biliVideoUrl.substring(biliVideoUrl.lastIndexOf("/av") + 3, biliVideoUrl.lastIndexOf("/"));
                    String page ;
                    if (biliVideoUrl.contains("_"))
                        page=biliVideoUrl.substring(biliVideoUrl.lastIndexOf("_")+1,biliVideoUrl.lastIndexOf(".html"));
                    else
                        page="1";
                    RetrofitManager retrofitManager = RetrofitManager.getInstance();
                    APIService apiService = retrofitManager.create(HttpConstant.BiliBili_CID_GET);
                    retrofitManager.enqueue(apiService.getBiliBiliCid(avnum,page), new Callback<CidResponse>() {
                        @Override
                        public void onResponse(Call<CidResponse> call, Response<CidResponse> response) {
                            CidResponse cidResponse = response.body();
                            //获取xml弹幕
                            getBiliBiliComment(cidResponse.getCid());
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            LogUtils.e("VideoViewPresenter", "CidResponse Error", t);
                        }
                    });
                }
                if (relatedsBean.getProvider().contains("Acfun")) {
                    //按bilibili解析弹幕
                    String acVideoUrl = relatedsBean.getUrl();
                    if (TextUtils.isEmpty(acVideoUrl))
                        return;
                    String acnum = acVideoUrl.substring(acVideoUrl.lastIndexOf("/ac") + 3);
                    String page ;
                    if (acnum.contains("_")){
                        page=acnum.substring(acnum.lastIndexOf("_")+1);
                        acnum=acnum.substring(0,acnum.lastIndexOf("_"));
                    } else
                        page="1";
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    Request.Builder requestBuilder = new Request.Builder().url(HttpConstant.ACFUN_COMMENT_BASE_URL+"comment_list_json.aspx?isNeedAllCount=true&contentId="+acnum+"&currentPage="+page);
                    Request request = requestBuilder.build();
                    okhttp3.Call mcall= mOkHttpClient.newCall(request);
                    mcall.enqueue(new okhttp3.Callback() {

                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            LogUtils.e("VideoViewPresenter", "acfuncomment Request Error", e);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            if (response.isSuccessful()){
                                String jsonstr=response.body().string();
                                jsonstr=jsonstr.replace("\"commentContentArr\":{","\"commentContentArr\":[").replace("}}}}","}]}}").replaceAll("\"c\\d+\":","");
                                //解析
                                System.out.println(jsonstr);
                            }else {
                                LogUtils.e("VideoViewPresenter", "bilicomment Error: server error");
                            }
                        }
                    });

                }
                if (relatedsBean.getProvider().contains("Tucao")) {

                }
            }
        }

    }
    private void getBiliBiliComment(final String cid){

        OkHttpClient mOkHttpClient=new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(HttpConstant.BILIBILIJIJI_COMMENT_BASE_URL+cid+"&n="+cid+".xml");
        Request request = requestBuilder.build();
        okhttp3.Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtils.e("VideoViewPresenter", "bilicomment Error", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()){
//                    byte[] b = response.body().bytes();     //获取数据的bytes
//                    String info = new String(b, "GB2312");//然后将其转为gb2312
//                    LogUtils.e("charset",info);
                    String xmlstr=response.body().string();
                    parseXMLWithSAX(xmlstr);
                }else {
                    LogUtils.e("VideoViewPresenter", "bilicomment Error: server error");
                }
            }
        });
    }
//    // 以下是测试字符编码的
//    public static void testCharset(String datastr){
//        try {
//            String temp = new String(datastr.getBytes(), "GBK");
//            Log.v("TestCharset","****** getBytes() -> GBK ******/n"+temp);
//            temp = new String(datastr.getBytes("GBK"), "UTF-8");
//            Log.v("TestCharset","****** GBK -> UTF-8 *******/n"+temp);
//            temp = new String(datastr.getBytes("GBK"), "ISO-8859-1");
//            Log.v("TestCharset","****** GBK -> ISO-8859-1 *******/n"+temp);
//            temp = new String(datastr.getBytes("ISO-8859-1"), "UTF-8");
//            Log.v("TestCharset","****** ISO-8859-1 -> UTF-8 *******/n"+temp);
//            temp = new String(datastr.getBytes("ISO-8859-1"), "GBK");
//            Log.v("TestCharset","****** ISO-8859-1 -> GBK *******/n"+temp);
//            temp = new String(datastr.getBytes("UTF-8"), "GBK");
//            Log.v("TestCharset","****** UTF-8 -> GBK *******/n"+temp);
//            temp = new String(datastr.getBytes("UTF-8"), "ISO-8859-1");
//            Log.v("TestCharset","****** UTF-8 -> ISO-8859-1 *******/n"+temp);
//            temp = new String(datastr.getBytes("UTF-8"), "GB2312");
//            Log.v("TestCharset","****** UTF-8 -> GB3212 *******/n"+temp);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
    private void parseXMLWithSAX(String xmlData){
        try{
            //SAX解析的工厂对象
            SAXParserFactory factory=SAXParserFactory.newInstance();
            //得到sax的解析器
            SAXParser saxParser=factory.newSAXParser();
            //创建handler对象
            SAXContentHandler handlerService=new SAXContentHandler(mView);
            InputStream is = new ByteArrayInputStream(xmlData.getBytes());
            //直接解析
            saxParser.parse(is, handlerService);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

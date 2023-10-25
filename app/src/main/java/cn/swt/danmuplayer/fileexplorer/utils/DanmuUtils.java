package cn.swt.danmuplayer.fileexplorer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.swt.corelib.utils.FileUtils;
import com.swt.corelib.utils.LogUtils;
import com.swt.corelib.utils.ProgressDialogUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import cn.swt.danmuplayer.application.MyApplication;
import cn.swt.danmuplayer.core.http.APIService;
import cn.swt.danmuplayer.core.http.HttpConstant;
import cn.swt.danmuplayer.core.http.RetrofitManager;
import cn.swt.danmuplayer.core.http.beans.CidResponse;
import cn.swt.danmuplayer.core.http.beans.CommentResponse;
import cn.swt.danmuplayer.core.http.beans.RelatedResponse;
import cn.swt.danmuplayer.fileexplorer.beans.DanmakuBean;
import cn.swt.danmuplayer.fileexplorer.beans.VideoFileArgInfo;
import cn.swt.danmuplayer.fileexplorer.view.EpisodeIdMatchActivity;
import cn.swt.danmuplayer.play.view.VideoViewActivity;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Title: DanmuUtils <br>
 * Description: 弹幕存储器<br>
 * Created DateTime: 2017-1-13 13:59
 * Created by Hienao
 */
public class DanmuUtils {
    private  Context mContext;
    private  List<DanmakuBean> mDanmakuBeanList;
    private  String videoPath, fileTitle, title;
    private  int episode_id;
    private  boolean getDanDanComment=false,getOtherCommentSource=false;
    private  int otherCommentCount=0,otherCommentNum=-1;
    public DanmuUtils(Context context, String videoPath, String fileTitle, String title, int episodeId) {
        mContext=context;
        this.videoPath=videoPath;
        this.fileTitle=fileTitle;
        this.title=title;
        this.episode_id=episodeId;
        mDanmakuBeanList = new ArrayList<>();
    }

    /**
     * 判断弹幕获取完成状态
     */
    public synchronized void judgeCommentState(){
        LogUtils.i("判断弹幕获取状态：dandan："+getDanDanComment+" toher:"+getOtherCommentSource+" "+otherCommentCount+"of "+otherCommentNum);
        if (getDanDanComment&&getOtherCommentSource&&(otherCommentCount>=otherCommentNum)){
            if (!TextUtils.isEmpty(videoPath)){
                String xmlpath=videoPath.substring(0, videoPath.lastIndexOf(".")) + "dd.xml";
                exportDanmuList2Xml(xmlpath);
            }
        }
    }
    public  synchronized void addCommentCount(){
        otherCommentCount++;
        judgeCommentState();
    }

    public void clearDanmulist() {
        mDanmakuBeanList.clear();
    }

    public void addDanmu(DanmakuBean danmakuBean) {
        mDanmakuBeanList.add(danmakuBean);
    }

    public void addDanmuList(List<DanmakuBean> danmakuBeanList) {
        if (danmakuBeanList != null && danmakuBeanList.size() != 0) {
            mDanmakuBeanList.addAll(danmakuBeanList);
        }
    }

    public Context getmContext() {
        return mContext;
    }
    /**
     * 将弹幕列表输出到指定的xml文件
     *
     * @param xmlpath xml文件路径
     */
    private void exportDanmuList2Xml(String xmlpath) {
        if (mDanmakuBeanList==null||mDanmakuBeanList.size()==0){
            LogUtils.i("没有获取到弹幕");
            danmuGetFinish();
            return;
        }
        LogUtils.i("开始输出xml");
        StringWriter xmlWriter = new StringWriter();
        try {
            SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
            TransformerHandler handler = factory.newTransformerHandler();

            Transformer transformer = handler.getTransformer();     // 设置xml属性
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            StreamResult result = new StreamResult(xmlWriter);      // 保存创建的xml
            handler.setResult(result);
            handler.startDocument();
            AttributesImpl attr = new AttributesImpl();
            attr.clear();
            handler.startElement("", "", "i", attr);

            attr.clear();
            handler.startElement("", "", "chatserver", attr);
            String chatserver = "chat.bilibili.com";
            handler.characters(chatserver.toCharArray(), 0, chatserver.length());
            handler.endElement("", "", "chatserver");

            attr.clear();
            handler.startElement("", "", "chatid", attr);
            String chatid = String.valueOf(episode_id);
            handler.characters(chatid.toCharArray(), 0, chatid.length());
            handler.endElement("", "", "chatid");

            attr.clear();
            handler.startElement("", "", "mission", attr);
            String mission = "0";
            handler.characters(mission.toCharArray(), 0, mission.length());
            handler.endElement("", "", "mission");

            attr.clear();
            handler.startElement("", "", "maxlimit", attr);
            String maxlimit = String.valueOf(mDanmakuBeanList.size());
            handler.characters(maxlimit.toCharArray(), 0, maxlimit.length());
            handler.endElement("", "", "maxlimit");

            attr.clear();
            handler.startElement("", "", "source", attr);
            String source = "k-v";
            handler.characters(source.toCharArray(), 0, source.length());
            handler.endElement("", "", "source");

            for (DanmakuBean exportDanmuBean:mDanmakuBeanList){
                attr.clear();
                String attribute=exportDanmuBean.getTime()+","+exportDanmuBean.getType()+","+
                        exportDanmuBean.getTextSize()+","+exportDanmuBean.getTextColor()+","+
                        exportDanmuBean.getSendtimeunix()+","+exportDanmuBean.getPriority()+","+
                        exportDanmuBean.getUserId()+","+exportDanmuBean.getIndex();
                attr.addAttribute("", "", "p", "", attribute);
                handler.startElement("", "", "d", attr);
                String text = exportDanmuBean.getText();
                handler.characters(text.toCharArray(), 0, text.length());
                handler.endElement("", "", "d");
            }
            handler.endElement("", "", "i");
            handler.endDocument();
            if(FileUtils.createFileByDeleteOldFile(xmlpath)){
                if (FileUtils.writeFileFromString(FileUtils.getFileByPath(xmlpath),xmlWriter.toString(),true)){
                    LogUtils.i("弹幕保存完成");
                    Realm realm = MyApplication.getRealmInstance();
                    VideoFileArgInfo videoFileArgInfo=realm.where(VideoFileArgInfo.class).equalTo("videoPath", videoPath).findFirst();
                    realm.beginTransaction();
                    if (videoFileArgInfo==null){
                        videoFileArgInfo=new VideoFileArgInfo();
                        videoFileArgInfo.setVideoPath(videoPath);
                        videoFileArgInfo.setSawProgress(0);
                        videoFileArgInfo.setHaveLocalDanmu(true);
                        realm.copyToRealm(videoFileArgInfo);
                    }else {
                        videoFileArgInfo.setHaveLocalDanmu(true);
                    }
                    realm.commitTransaction();
                    danmuGetFinish();
                }
            }
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        danmuGetFinish();

    }

    /**
     * 从dandanpaly识别到的视频id获取所有网站弹幕信息
     *
     */
    public void getDanmuListByEspoisedId() {
        getComment(String.valueOf(episode_id),"0");
        getCommentSource(String.valueOf(episode_id));
    }

    /**
     * 获取dandanplay弹幕
     * @param episodeId
     * @param from
     */
    private void getComment(String episodeId, String from) {
        LogUtils.i("开始获取弹弹弹幕");
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getComment(episodeId, from), new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if (commentResponse != null) {
                    if (commentResponse == null || commentResponse.getComments() == null || commentResponse.getComments().size() == 0) {
                        getDanDanComment=true;
                        judgeCommentState();
                    } else {
                        List<CommentResponse.CommentsBean> commentsBeanList = commentResponse.getComments();
                        if (commentsBeanList != null && commentsBeanList.size() != 0) {
                            for (CommentResponse.CommentsBean commentsBean : commentsBeanList) {
                                DanmakuBean danmakuBean=new DanmakuBean();
                                danmakuBean.setTime(String.valueOf(commentsBean.getTime()));
                                danmakuBean.setType(String.valueOf(commentsBean.getMode()));
                                danmakuBean.setTextSize(String.valueOf(25));
                                danmakuBean.setTextColor(String.valueOf(commentsBean.getColor()));
                                danmakuBean.setSendtimeunix(String.valueOf(commentsBean.getTimestamp()));
                                danmakuBean.setPriority(String.valueOf(commentsBean.getPool()));
                                danmakuBean.setUserId(String.valueOf(commentsBean.getUId()));
                                danmakuBean.setIndex(String.valueOf(commentsBean.getCId()));
                                danmakuBean.setText(commentsBean.getMessage());
                                if (danmakuBean.isFull()){
                                    mDanmakuBeanList.add(danmakuBean);
                                }
                            }
                            getDanDanComment=true;
                            judgeCommentState();
                        }
                    }
                } else {
                    getDanDanComment=true;
                    judgeCommentState();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                LogUtils.i("commentResponse Error", t);
            }
        });
    }

    /**
     * 获取第三方弹幕信息
     * @param episodeId
     */
    private void getCommentSource(String episodeId) {
        LogUtils.i("开始获取第三方弹幕信息");
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
        APIService apiService = retrofitManager.create();
        retrofitManager.enqueue(apiService.getCommentSource(episodeId), new Callback<RelatedResponse>() {
            @Override
            public void onResponse(Call<RelatedResponse> call, Response<RelatedResponse> response) {
                RelatedResponse relatedResponse = response.body();
                List<RelatedResponse.RelatedsBean> relatedsBeanList=relatedResponse.getRelateds();
                if (relatedsBeanList==null||relatedsBeanList.size()==0){
                    otherCommentNum=0;
                    otherCommentCount=0;
                }else {
                    otherCommentNum=relatedsBeanList.size();
                    otherCommentCount=0;
                }
                getOtherCommentSource=true;
                judgeCommentState();
                getOtherComment(relatedResponse.getRelateds());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                otherCommentNum=0;
                otherCommentCount=0;
                getOtherCommentSource=true;
                judgeCommentState();
                LogUtils.e("commentResponse Error", t);
            }
        });

    }

    /**
     * 获取第三方弹幕源弹幕
     *
     * @param relatedsBeanList
     */
    private void getOtherComment(List<RelatedResponse.RelatedsBean> relatedsBeanList) {
        LogUtils.i("开始获取第三方弹幕");
        if (relatedsBeanList != null && relatedsBeanList.size() != 0) {
            dereplicationDanmuSource(relatedsBeanList);
            for (RelatedResponse.RelatedsBean relatedsBean : relatedsBeanList) {
                if (relatedsBean.getProvider().contains("BiliBili")&&relatedsBean.getUrl().contains("http://www.bilibili.com/video/")) {
                    LogUtils.i("开始获取B站弹幕信息");
                    //按bilibili解析弹幕
                    String biliVideoUrl = relatedsBean.getUrl();
                    if (TextUtils.isEmpty(biliVideoUrl))
                        return;
                    String avnum = biliVideoUrl.substring(biliVideoUrl.lastIndexOf("/av") + 3, biliVideoUrl.lastIndexOf("/"));
                    String page;
                    if (biliVideoUrl.contains("_"))
                        page = biliVideoUrl.substring(biliVideoUrl.lastIndexOf("_") + 1, biliVideoUrl.lastIndexOf(".html"));
                    else
                        page = "1";
                    RetrofitManager retrofitManager = RetrofitManager.getInstance();
                    APIService apiService = retrofitManager.create(HttpConstant.BiliBili_CID_GET);
                    retrofitManager.enqueue(apiService.getBiliBiliCid(avnum, page), new Callback<CidResponse>() {
                        @Override
                        public void onResponse(Call<CidResponse> call, Response<CidResponse> response) {
                            CidResponse cidResponse = response.body();
                            if (cidResponse != null) {
                                //获取xml弹幕
                                getBiliBiliComment2(cidResponse.getCid());
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            LogUtils.e("CidResponse Error", t);
                            otherCommentCount++;
                            judgeCommentState();
                        }
                    });
                } else if (relatedsBean.getProvider().contains("Acfun")) {
                    LogUtils.i("开始获取A站弹幕信息");
                    RetrofitManager retrofitManager = RetrofitManager.getInstance();
                    APIService apiService = retrofitManager.create(HttpConstant.ACPLAY_BASE_URL);
                    retrofitManager.enqueue(apiService.getOtherCommentByVideoUrl(relatedsBean.getUrl()), new Callback<CommentResponse>() {
                        @Override
                        public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                            CommentResponse commentResponse = response.body();
                            if (commentResponse != null) {
                                if (commentResponse == null || commentResponse.getComments() == null || commentResponse.getComments().size() == 0) {
                                    otherCommentCount++;
                                    judgeCommentState();
                                } else {
                                    List<CommentResponse.CommentsBean> commentsBeanList = commentResponse.getComments();
                                    if (commentsBeanList != null && commentsBeanList.size() != 0) {
                                        for (CommentResponse.CommentsBean commentsBean : commentsBeanList) {
                                            DanmakuBean danmakuBean=new DanmakuBean();
                                            danmakuBean.setTime(String.valueOf(commentsBean.getTime()));
                                            danmakuBean.setType(String.valueOf(commentsBean.getMode()));
                                            danmakuBean.setTextSize(String.valueOf(25));
                                            danmakuBean.setTextColor(String.valueOf(commentsBean.getColor()));
                                            danmakuBean.setSendtimeunix(String.valueOf(commentsBean.getTimestamp()));
                                            danmakuBean.setPriority(String.valueOf(commentsBean.getPool()));
                                            danmakuBean.setUserId(String.valueOf(commentsBean.getUId()));
                                            danmakuBean.setIndex(String.valueOf(commentsBean.getCId()));
                                            danmakuBean.setText(commentsBean.getMessage());
                                            if (danmakuBean.isFull()){
                                                mDanmakuBeanList.add(danmakuBean);
                                            }
                                        }
                                        otherCommentCount++;
                                        judgeCommentState();
                                    }
                                }
                            } else {
                                otherCommentCount++;
                                judgeCommentState();
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentResponse> call, Throwable t) {
                            otherCommentCount++;
                            judgeCommentState();
                        }
                    });
                } else if (relatedsBean.getProvider().contains("Tucao")) {
                    LogUtils.i("开始获取c站弹幕信息");
//                    getTuCaoCommentURL(relatedsBean.getUrl());
                    RetrofitManager retrofitManager = RetrofitManager.getInstance();
                    APIService apiService = retrofitManager.create(HttpConstant.ACPLAY_BASE_URL);
                    retrofitManager.enqueue(apiService.getOtherCommentByVideoUrl(relatedsBean.getUrl()), new Callback<CommentResponse>() {
                        @Override
                        public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                            CommentResponse commentResponse = response.body();
                            if (commentResponse != null) {
                                if (commentResponse == null || commentResponse.getComments() == null || commentResponse.getComments().size() == 0) {
                                    otherCommentCount++;
                                    judgeCommentState();
                                } else {
                                    List<CommentResponse.CommentsBean> commentsBeanList = commentResponse.getComments();
                                    if (commentsBeanList != null && commentsBeanList.size() != 0) {
                                        for (CommentResponse.CommentsBean commentsBean : commentsBeanList) {
                                            DanmakuBean danmakuBean=new DanmakuBean();
                                            danmakuBean.setTime(String.valueOf(commentsBean.getTime()));
                                            danmakuBean.setType(String.valueOf(commentsBean.getMode()));
                                            danmakuBean.setTextSize(String.valueOf(25));
                                            danmakuBean.setTextColor(String.valueOf(commentsBean.getColor()));
                                            danmakuBean.setSendtimeunix(String.valueOf(commentsBean.getTimestamp()));
                                            danmakuBean.setPriority(String.valueOf(commentsBean.getPool()));
                                            danmakuBean.setUserId(String.valueOf(commentsBean.getUId()));
                                            danmakuBean.setIndex(String.valueOf(commentsBean.getCId()));
                                            danmakuBean.setText(commentsBean.getMessage());
                                            if (danmakuBean.isFull()){
                                                mDanmakuBeanList.add(danmakuBean);
                                            }
                                        }
                                        otherCommentCount++;
                                        judgeCommentState();
                                    }
                                }
                            } else {
                                otherCommentCount++;
                                judgeCommentState();
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentResponse> call, Throwable t) {
                            otherCommentCount++;
                            judgeCommentState();
                        }
                    });
                } else {
                    otherCommentCount++;
                    judgeCommentState();
                }
            }
        }

    }
    /**
     * 获取吐槽网弹幕URL
     *
     * @param url
     */
    private void getTuCaoCommentURL(String url) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        Request request = requestBuilder.build();
        okhttp3.Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtils.e("tucaocomment Error", e);
                otherCommentCount++;
                judgeCommentState();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String htmlstr = response.body().string();
                    String pattern = "http://www.tucao.tv/index.php\\?m=mukio.*?\"";
                    // 创建 Pattern 对象
                    Pattern r = Pattern.compile(pattern);

                    // 现在创建 matcher 对象
                    Matcher m = r.matcher(htmlstr);
                    if (m.find()) {
                        String danmuurl = m.group(0).substring(0, m.group(0).length() - 1).replace("tj", "init");
                        getTuCaoComments(danmuurl);
                    } else {
                        otherCommentCount++;
                        judgeCommentState();
                    }
                } else {
                    otherCommentCount++;
                    judgeCommentState();
                    LogUtils.e("tucaocomment Error: server error");
                }
            }
        });
    }

    /**
     * 获取吐槽网弹幕信息
     *
     * @param danmuurl
     */
    private void getTuCaoComments(String danmuurl) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(danmuurl);
        Request request = requestBuilder.build();
        okhttp3.Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtils.e("tucaocomment Error", e);
                otherCommentCount++;
                judgeCommentState();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String xmlstr = response.body().string();
                    parseTucaoCommentsXMLWithSAX(xmlstr);
                } else {
                    LogUtils.e( "tucaocomment Error: server error");
                    otherCommentCount++;
                    judgeCommentState();
                }
            }
        });
    }

    private void parseTucaoCommentsXMLWithSAX(String xmlstr) {
        try {
            //SAX解析的工厂对象
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //得到sax的解析器
            SAXParser saxParser = factory.newSAXParser();
            //创建handler对象
            TucaoSAXContentHandler handlerService = new TucaoSAXContentHandler(mContext);
            InputStream is = new ByteArrayInputStream(xmlstr.getBytes());
            //直接解析
            saxParser.parse(is, handlerService);
        } catch (Exception e) {
            otherCommentCount++;
            judgeCommentState();
            e.printStackTrace();
        }
    }

    /**
     * 去除重复弹幕源
     *
     * @param relatedsBeanList
     */
    private void dereplicationDanmuSource(List<RelatedResponse.RelatedsBean> relatedsBeanList) {
        ArrayList sourceList = new ArrayList();
        for (RelatedResponse.RelatedsBean relatedsBean : relatedsBeanList) {
            if (sourceList.contains(relatedsBean.getProvider())||relatedsBean.getUrl().contains("http://bangumi.bilibili.com/anime/v/")) {
                relatedsBean.setProvider("repeat");
            } else {
                sourceList.add(relatedsBean.getProvider());
            }
        }
    }
    private void getBiliBiliComment2( String cid) {
        WebView mWebview= ((EpisodeIdMatchActivity) mContext).getWebview();
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        mWebview.setWebViewClient(new CustomWebViewClient());
        mWebview.loadUrl(HttpConstant.BILIBILI_COMMENT_BASE_URL + cid + ".xml");
    }
    private void parseBiliCommentsXMLWithSAX(String xmlData) {
        try {
            //SAX解析的工厂对象
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //得到sax的解析器
            SAXParser saxParser = factory.newSAXParser();
            //创建handler对象
            BiliSAXContentHandler handlerService = new BiliSAXContentHandler(mContext);
            InputStream is = new ByteArrayInputStream(xmlData.getBytes());
            //直接解析
            saxParser.parse(is, handlerService);
        } catch (Exception e) {
            e.printStackTrace();
            otherCommentCount++;
            judgeCommentState();
        }

    }
    /**
     * 弹幕获取完成之后的操作，一般为跳转
     */
    public  void danmuGetFinish() {
        LogUtils.i("准备跳转到播放界面");
        new Handler().postDelayed(new Runnable(){
            public void run() {
                ProgressDialogUtils.dismissDialog();
                clearDanmulist();
                if (mContext != null) {
                    mContext.startActivity(new Intent(mContext, VideoViewActivity.class)
                            .putExtra("path", videoPath)
                            .putExtra("file_title", fileTitle)
                            .putExtra("title", title).putExtra("episode_id", episode_id));
                }
            }
        }, 1000);

    }
    final class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.java_obj.getSource('<i>'+" +
                    "document.getElementsByTagName('i')[0].innerHTML+'</i>');");
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            otherCommentCount++;
            judgeCommentState();
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getSource(String xml) {
            String addheadxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
            if (videoPath != null) {
                String xmlfilepath = videoPath.substring(0, videoPath.lastIndexOf(".")) + ".xml";
                if (FileUtils.createFileByDeleteOldFile(xmlfilepath)){
                    if (FileUtils.writeFileFromString(xmlfilepath, addheadxml, true)){
                        String xmlstr = FileUtils.readFile2String(xmlfilepath, "UTF-8");
                        parseBiliCommentsXMLWithSAX(xmlstr);
                    }
                }
            }

        }
    }
    class BiliSAXContentHandler extends DefaultHandler {
        //声明标签的名称
        public String tagName;
        private Context mContext;
        private DanmakuBean danmakuBean;

        public BiliSAXContentHandler(Context context) {
            mContext=context;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            danmakuBean = null;
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            otherCommentCount++;
            judgeCommentState();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("d")) {
                try {
                    String attaibute = attributes.getValue(0);
                    String[] sourceStrArray = attaibute.split(",");
                    danmakuBean = new DanmakuBean();
                    danmakuBean.setTime(sourceStrArray[0]);
                    danmakuBean.setType(sourceStrArray[1]);
                    danmakuBean.setTextSize(sourceStrArray[2]);
                    danmakuBean.setTextColor(sourceStrArray[3]);
                    danmakuBean.setSendtimeunix(sourceStrArray[4]);
                    danmakuBean.setPriority(sourceStrArray[5]);
                    danmakuBean.setUserId(sourceStrArray[6]);
                    danmakuBean.setIndex(sourceStrArray[7]);
                    tagName = localName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (danmakuBean != null && localName.equals("d")) {
                if (danmakuBean.isFull()){
                    mDanmakuBeanList.add(danmakuBean);
                }
                danmakuBean = null;
            }
            super.endElement(uri, localName, qName);
            tagName = null;

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            //首先判断tagName是否为空
            if (tagName != null) {
                String data = new String(ch, start, length);
                //判断标签是否为空
                if (tagName.equals("d")) {
                    if (danmakuBean != null) {
                        danmakuBean.setText(data);
                    }
                }
            }
        }
    }

    class TucaoSAXContentHandler extends DefaultHandler {
        //声明标签的名称
        public String tagName;
        private Context mContext;
        private DanmakuBean danmakuBean;

        public TucaoSAXContentHandler(Context context) {
            mContext = context;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            danmakuBean = null;
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            otherCommentCount++;
            judgeCommentState();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("d")) {
                try {
                    String attaibute = attributes.getValue(0);
                    String[] sourceStrArray = attaibute.split(",");
                    danmakuBean = new DanmakuBean();
                    danmakuBean.setTime(sourceStrArray[0]);
                    danmakuBean.setType(sourceStrArray[1]);
                    danmakuBean.setTextSize(sourceStrArray[2]);
                    danmakuBean.setTextColor(sourceStrArray[3]);
                    danmakuBean.setSendtimeunix(sourceStrArray[4]);
                    danmakuBean.setPriority("0");
                    danmakuBean.setUserId("-1");
                    danmakuBean.setIndex(sourceStrArray[4]);
                    tagName = localName;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (danmakuBean != null && localName.equals("d")) {
                if (danmakuBean.isFull()){
                    mDanmakuBeanList.add(danmakuBean);
                }
                danmakuBean = null;
            }
            super.endElement(uri, localName, qName);
            tagName = null;

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            //首先判断tagName是否为空
            if (tagName != null) {
                String data = new String(ch, start, length);
                //判断标签是否为空
                if (tagName.equals("d")) {
                    if (danmakuBean != null) {
                        danmakuBean.setText(data);
                    }
                }
            }
        }
    }

}

package cn.swt.dandanplay.core.http;

import android.util.Log;

import com.swt.corelib.utils.ColorUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import cn.swt.dandanplay.core.http.beans.CommentResponse;

/**
 * Title: SAXContentHandler <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/12/25 0025 22:50
 * Created by Wentao.Shi.
 */
public class SAXContentHandler extends DefaultHandler {
    //声明标签的名称
    public String tagName;
    private List<CommentResponse.CommentsBean> mBiliCommentsBeanList ;
    private CommentResponse.CommentsBean mCommentsBean;
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        mBiliCommentsBeanList =new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        mBiliCommentsBeanList=null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("d")) {
            mCommentsBean=new CommentResponse.CommentsBean();
            String attaibute=attributes.getValue(0);
            String[] sourceStrArray = attaibute.split(",");
            mCommentsBean.setTime(Double.parseDouble(sourceStrArray[0]));//设置弹幕出现的时间 以秒数为单位。
            mCommentsBean.setMode(Integer.parseInt(sourceStrArray[1]));//弹幕的模式
            int color= ColorUtils.bilicolor2dandancolor(sourceStrArray[2]);
            if (color!=-1){
                mCommentsBean.setColor(color);
            }
            Log.e("sax",attributes.getValue(0));
            tagName=localName;
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        tagName=null;
        mCommentsBean=null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        //首先判断tagName是否为空
        if(tagName!=null){
            String data=new String(ch,start,length);
            //判断标签是否为空
            if(tagName.equals("d")){
                if (mCommentsBean!=null){
                    mCommentsBean.setMessage(data);
                }
            }
        }
    }
}

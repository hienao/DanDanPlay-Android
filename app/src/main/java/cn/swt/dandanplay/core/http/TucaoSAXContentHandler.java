package cn.swt.dandanplay.core.http;

import android.content.Context;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.swt.dandanplay.fileexplorer.beans.DanmakuBean;
import cn.swt.dandanplay.fileexplorer.utils.DanmuUtils;

/**
 * Title: BiliSAXContentHandler <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/12/25 0025 22:50
 * Created by Wentao.Shi.
 */
public class TucaoSAXContentHandler extends DefaultHandler {
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
        DanmuUtils.getInstance(mContext).addCommentCount();
        DanmuUtils.getInstance(mContext).judgeCommentState();
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
                DanmuUtils.getInstance(mContext).addDanmu(danmakuBean);
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

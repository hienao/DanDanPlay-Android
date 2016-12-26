package cn.swt.dandanplay.core.http;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.swt.dandanplay.play.contract.VideoViewContract;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;

/**
 * Title: SAXContentHandler <br>
 * Description: <br>
 * Copyright (c) 传化物流版权所有 2016 <br>
 * Created DateTime: 2016/12/25 0025 22:50
 * Created by Wentao.Shi.
 */
public class SAXContentHandler extends DefaultHandler {
    //声明标签的名称
    public String                        tagName;
    private DanmakuContext mDanmakuContext;
    private VideoViewContract.View mView;
    private String []strarr;

    public SAXContentHandler(VideoViewContract.View view) {
        mView=view;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        strarr=new String[]{"","","","","","","","",""};
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("d")) {
            try{
                String attaibute=attributes.getValue(0);
                String[] sourceStrArray = attaibute.split(",");
                strarr[0]=sourceStrArray[0];strarr[1]=sourceStrArray[1];strarr[2]=sourceStrArray[2];
                strarr[3]=sourceStrArray[3];strarr[4]=sourceStrArray[4];strarr[5]=sourceStrArray[5];
                strarr[6]=sourceStrArray[6];strarr[7]=sourceStrArray[7];
                tagName=localName;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (strarr!=null&&localName.equals("d")){
            mView.addBiliBiliDanmu(strarr[0],strarr[1],strarr[2],strarr[3],
                    strarr[4], strarr[5],strarr[6],strarr[7],strarr[8]);
            for (String s:strarr){
                s="";
            }
        }
        super.endElement(uri, localName, qName);
        tagName=null;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        //首先判断tagName是否为空
        if(tagName!=null){
            String data=new String(ch,start,length);
            //判断标签是否为空
            if(tagName.equals("d")){
                if (strarr!=null){
                    strarr[8]=data;
                }
            }
        }
    }
}

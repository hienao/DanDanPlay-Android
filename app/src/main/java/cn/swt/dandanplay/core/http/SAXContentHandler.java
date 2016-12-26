package cn.swt.dandanplay.core.http;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
    private StringBuffer buffer = new StringBuffer();
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("d")) {
            Log.e("sax",attributes.getValue(0));
            tagName=localName;
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
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
                Log.e("sax",data);
            }
        }
    }
}

package com.example.dellpc.network;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by admin on 2015-08-16.
 */
/*
SAX解析方法需要创建一个继承自DefaultHandler类的一个类
 */
public class MyHandler extends DefaultHandler {

    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    /*
    在开始Xml解析的时候调用
     */
    @Override
    public void startDocument() throws SAXException {
        id=new StringBuilder();
        name=new StringBuilder();
        version=new StringBuilder();
    }

    /*
    在开始解析某个节点的时候调用
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //记录当前节点名称
        nodeName=localName;
    }


    /*
    在获取节点中内容的时候调用
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //根据当前的节点名判断将内容添加到哪一个StringBuilder对象中
        if(nodeName.equals("id")){
            id.append(ch,start,length);
        }else if(nodeName.equals("name")){
            name.append(ch,start,length);
        }else if(nodeName.equals("version")){
            version.append(ch,start,length);
        }
    }

    /*
  在节点解析结束的时候调用
   */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //因为有可能包含换行符或回车，因此在打印之前调用一下trim方法
        if(localName.equals("app")){
            Log.d("ContentHandle","id is "+id.toString().trim());
            Log.d("ContentHandle","name is "+name.toString().trim());
            Log.d("ContentHandle","version is "+version.toString().trim());
            //最后将StringBuilder清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }


    /*
    在完成整个Xml解析的时候调用
     */
    @Override
    public void endDocument() throws SAXException {

    }


}

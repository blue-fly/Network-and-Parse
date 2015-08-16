package com.example.dellpc.network;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by admin on 2015-08-16.
 */
/*
SAX����������Ҫ����һ���̳���DefaultHandler���һ����
 */
public class MyHandler extends DefaultHandler {

    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    /*
    �ڿ�ʼXml������ʱ�����
     */
    @Override
    public void startDocument() throws SAXException {
        id=new StringBuilder();
        name=new StringBuilder();
        version=new StringBuilder();
    }

    /*
    �ڿ�ʼ����ĳ���ڵ��ʱ�����
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //��¼��ǰ�ڵ�����
        nodeName=localName;
    }


    /*
    �ڻ�ȡ�ڵ������ݵ�ʱ�����
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //���ݵ�ǰ�Ľڵ����жϽ�������ӵ���һ��StringBuilder������
        if(nodeName.equals("id")){
            id.append(ch,start,length);
        }else if(nodeName.equals("name")){
            name.append(ch,start,length);
        }else if(nodeName.equals("version")){
            version.append(ch,start,length);
        }
    }

    /*
  �ڽڵ����������ʱ�����
   */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //��Ϊ�п��ܰ������з���س�������ڴ�ӡ֮ǰ����һ��trim����
        if(localName.equals("app")){
            Log.d("ContentHandle","id is "+id.toString().trim());
            Log.d("ContentHandle","name is "+name.toString().trim());
            Log.d("ContentHandle","version is "+version.toString().trim());
            //���StringBuilder��յ�
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }


    /*
    ���������Xml������ʱ�����
     */
    @Override
    public void endDocument() throws SAXException {

    }


}

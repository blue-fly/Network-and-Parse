package com.example.dellpc.network;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends Activity implements View.OnClickListener {


    private static final int SHOW_RESPONSE = 0;
    private static final String TAG = "MainActivity";
    private Button send_request;
    private TextView responseText;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    //在这里进行ui刷新
                    responseText.setText(response);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取控件再添加点击事件
        send_request = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response);

        send_request.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request) {
            //发起网络请求
            //sendRequestWithHttpURLConnection();
            SendRequestWithHttpClient();
        }
    }


    /*
    使用HttpURLConnection进行网络连接
     */
    private void sendRequestWithHttpURLConnection() {
        //开启子线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) url.openConnection();
                    //从服务器那里获取数据
                    connection.setRequestMethod("GET");
                    //连接超时
                    connection.setConnectTimeout(8000);
                    //读取超时的毫秒数
                    connection.setReadTimeout(8000);

                    InputStream in = connection.getInputStream();

                    //下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;

                    message.obj = builder.toString();

                    //异步调用
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //关闭连接
                    if (connection != null) {
                        connection.disconnect();
                    }
                }


            }
        }) {
        }.start();
    }

    /*
    get_data.xml文件的内容如下
    <apps>
        <app>
            <id>1</id>
            <name>Google Maps</name>
            <version>1.0</version>
        </app>
        <app>
            <id>2</id>
            <name>Chrome</name>
            <version>2.1</version>
        </app>
        <app>
            <id>3</id>
            <name>Google Play</name>
            <version>2.3</version>
        </app>
    </apps>
     */
     /*
    使用HttpClient进行网络连接
     */
    private void SendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    //对于模拟器来说10.0.2.2就是电脑本机的IP地址
                    HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/get_data.xml");
                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //请求和响应都成功了
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String response = EntityUtils.toString(httpEntity, "utf-8");

//                        Message message=new Message();
//                        message.what=SHOW_RESPONSE;
//                        message.obj=response;
//
//                        handler.sendMessage(message);

                        //解析xml
                        //parseXMLWithPull(response);
                        //解析xml
                        parseXMLWithSAX(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void parseXMLWithSAX(String xmlData) {
        try {
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader= factory.newSAXParser().getXMLReader();

            //将自己实现的Handler类设置到XMLReader中
            ContentHandler handler=new MyHandler();
            xmlReader.setContentHandler(handler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    使用Pull方式对Xml进行解析
     */
    private void parseXMLWithPull(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            String id = "";
            String name = "";
            String version = "";
            int eventType = xpp.getEventType();
            //说明解析工作还没有做完
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xpp.getName();
                switch (eventType) {
                    //开始解析某个节点
                    case XmlPullParser.START_TAG: {
                        if (nodeName.equals("id")) {
                            id = xpp.nextText();
                        } else if (nodeName.equals("name")) {
                            name = xpp.nextText();
                        } else if (nodeName.equals("version")) {
                            version = xpp.nextText();
                        }
                        break;
                    }
                    //完成某个节点的标签</app>
                    case XmlPullParser.END_TAG: {
                        if (nodeName.equals("app")) {
                            Log.d(TAG, "id:" + id);
                            Log.d(TAG, "name:" + name);
                            Log.d(TAG, "version:" + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

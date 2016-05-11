
package com.anp.ulsanfood.gcm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HttpRequest implements Runnable {

    private String HttpURL;

    private String tagName;

    private Thread thread;

    private ArrayList<Dictionary> dicArray;

    HttpRequestListener eventListener;

    public HttpRequest(HttpRequestListener event, String url, String element) {
        HttpURL = url;
        tagName = element;
        eventListener = event;
        dicArray = new ArrayList<Dictionary>();
        start();
    }

    public void start() {
        synchronized (this) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        synchronized (this) {
            thread.interrupt();
            thread = null;
        }
    }

    public void run() {
        if (readXML() == true) {
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (eventListener != null) {
                    boolean isErr = false;
                    if (dicArray == null || (dicArray != null && dicArray.size() <= 0)) {
                        isErr = true;
                    }

                    eventListener.getRequestData(dicArray, isErr);
                }
            } else {
                if (eventListener != null) {
                    eventListener.httpRequestError();
                }
            }
        }
    };

    private boolean readXML() {
        try {
            Log.d("HttpRequest", "HttpURL : " + HttpURL);
            URL url = new URL(HttpURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if (conn != null) {

                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream in = conn.getInputStream();

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document dom = db.parse(in);
                    dom.getDocumentElement().normalize();
                    Element docEle = dom.getDocumentElement();

                    NodeList topNodeList = docEle.getElementsByTagName(tagName);

                    if (topNodeList != null && topNodeList.getLength() > 0) {
                        for (int i = 0; i < topNodeList.getLength(); i++) {
                            Element topElement = (Element)topNodeList.item(i);
                            NodeList curNodeList = topElement.getChildNodes();

                            Dictionary dic = new Dictionary();
                            if (curNodeList != null && curNodeList.getLength() > 0) {
                                for (int j = 0; j < curNodeList.getLength(); j++) {
                                    if (curNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        Element item = (Element)curNodeList.item(j);

                                        String itemName = null;
                                        String itemValue = null;
                                        if (item.getFirstChild() != null) {
                                            itemName = item.getNodeName();
                                            itemValue = item.getFirstChild().getNodeValue();
                                            itemValue = itemValue.replaceAll("^[\\s　]*", "")
                                                    .replaceAll("[\\s　]*$", "");
                                            dic.addString(itemName, itemValue);
                                            Log.d("HttpRequest", "itemName : " + itemName
                                                    + " / itemValue : " + itemValue);
                                        }
                                    }
                                }
                            }

                            dicArray.add(dic);
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            Log.d("HttpRequest", "Exception : " + e.toString());
            return false;
        }
    }

    /*
     * private boolean readXML() { try {
     * //Log.d("HttpRequest","HttpURL : "+HttpURL); URL url = new URL(HttpURL);
     * HttpURLConnection conn = (HttpURLConnection)url.openConnection();
     * if(conn!=null) { conn.setConnectTimeout(10000); conn.setUseCaches(false);
     * if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) { InputStream in =
     * conn.getInputStream(); DocumentBuilderFactory dbf =
     * DocumentBuilderFactory.newInstance(); DocumentBuilder db =
     * dbf.newDocumentBuilder(); Document dom = db.parse(in);
     * dom.getDocumentElement().normalize(); Element docEle =
     * dom.getDocumentElement(); NodeList topNodeList =
     * docEle.getElementsByTagName(tagName); if(topNodeList!=null &&
     * topNodeList.getLength()>0) { for(int i=0;i<topNodeList.getLength();i++) {
     * Element topElement = (Element)topNodeList.item(i); Dictionary dic =
     * getNodes(topElement); dicArray.add(dic); } } } } return true; }
     * catch(Exception e) { Log.e("HttpRequest","Exception : "+e.toString());
     * return false; } } public Dictionary getNodes(Element item) { NodeList
     * childNodes = item.getChildNodes(); Dictionary dic = new Dictionary();
     * if(childNodes != null && childNodes.getLength() > 0) {
     * ArrayList<Dictionary> childs = new ArrayList<Dictionary>(); for(int
     * i=0;i<childNodes.getLength();i++) { Dictionary childDic =
     * getNodes((Element)childNodes.item(i)); childs.add(childDic); }
     * dic.addObject(item.getNodeName(),childs);
     * dic.setType(item.getNodeName(),Dictionary.DATA_ARRAYLIST_DIC); } else {
     * getNodeValue(dic,item); } return dic; } public void
     * getNodeValue(Dictionary dic, Element item) {
     * if(item.getNodeType()==Node.ELEMENT_NODE) { String itemName = null;
     * String itemValue = null; if(item.getFirstChild()!=null) { itemName =
     * item.getNodeName(); itemValue = item.getFirstChild().getNodeValue();
     * itemValue =
     * itemValue.replaceAll("^[\\s　]*","").replaceAll("[\\s　]*$","");
     * dic.addString(itemName,itemValue);
     * Log.d("HttpRequest","itemName : "+itemName+" / itemValue : "+itemValue);
     * } } }
     */

    public interface HttpRequestListener {
        public void getRequestData(ArrayList<Dictionary> dicArray, boolean isError);

        public void httpRequestError();
    }

}

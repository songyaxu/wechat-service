package com.usforever.wechatservice.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.usforever.wechatservice.bo.PicMessage;
import com.usforever.wechatservice.bo.TextMessage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 17:07 2018/4/16
 * @Modified by:
 */

public class MessageUtil {

    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";
    public static final String RESP_MESSAGE_TYPE_IMAGE="image";
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final String REQ_MESSAGE_TYPE_LINK = "link";
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String EVENT_TYPE_CLICK = "CLICK";
    public static final String EVENT_TYPE_VIEW = "VIEW";

    private static MessageUtil messageUtil;

    private MessageUtil() {
    }

    public static MessageUtil getInstance() {
        if (messageUtil == null) {
            messageUtil = new MessageUtil();
        }
        return messageUtil;
    }

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    public Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        String requestXml = document.asXML();
        String subXml = requestXml.split(">")[0] + ">";
        requestXml = requestXml.substring(subXml.length());
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的全部子节点
        List<Element> elementList = root.elements();
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        map.put("requestXml", requestXml);
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 将请求的数据转化xml
     *
     * @param request
     * @return
     */
    public String parseMsgXml(HttpServletRequest request) {
        String responseMsg = null;
        try {
            InputStream is = request.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            responseMsg = new String(jsonBytes, "UTF-8");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMsg;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */

    public String textMessageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    /**
     * 图片消息对象转换成xml
     *
     * @param picMessage 图片消息对象
     * @return xml
     */
    public String picMessageToXml(PicMessage picMessage) {
        xstream.alias("xml", picMessage.getClass());
        return xstream.toXML(picMessage);

    }

    /**
     * 扩展xstream，使其支持CDATA块
     */
    private XStream xstream = new XStream(new XppDriver() {

        public HierarchicalStreamWriter createWriter(Writer out) {

            return new PrettyPrintWriter(out) {
                // 对全部xml节点的转换都添加CDATA标记
                boolean cdata = true;

                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                    if (name.equals("CreateTime")) {
                        cdata = false;
                    }
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }

            };

        }

    });
}

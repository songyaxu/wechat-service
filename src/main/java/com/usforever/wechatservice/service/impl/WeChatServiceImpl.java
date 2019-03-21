package com.usforever.wechatservice.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.usforever.wechatservice.bo.PicMessage;
import com.usforever.wechatservice.bo.TextMessage;
import com.usforever.wechatservice.service.GuavaCacheService;
import com.usforever.wechatservice.service.WeChatService;
import com.usforever.wechatservice.util.HttpUtil;
import com.usforever.wechatservice.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.usforever.wechatservice.common.Constants.GET_ACCESS_TOKEN_URL;
import static com.usforever.wechatservice.common.Constants.GET_USER_OPENID_AND_TOKEN;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 16:57 2018/4/16
 * @Modified by:
 */
@Slf4j
@Service("weChatService")
public class WeChatServiceImpl implements WeChatService {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    @Autowired
    private GuavaCacheService<String,String> guavaCacheService;

    private final String ACCESS_KEY = "ACCESS_KEY_YUNCAIYUAN";

    @Override
    public String getAccessToken() {
        String value = guavaCacheService.getIfPresent(ACCESS_KEY);
        if (StringUtils.isNotBlank(value)){
            return value;
        }
        String accessToken = null;
        String getUrl = GET_ACCESS_TOKEN_URL + "&appid=" + appId + "&secret=" + appSecret;
        try {
            String result = HttpUtil.get(getUrl);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (null != jsonObject) {
                try {
                    accessToken = jsonObject.getString("access_token");
                    //Long expiresIn = jsonObject.getLong("expires_in");
                    guavaCacheService.put(ACCESS_KEY,accessToken);
                } catch (JSONException e) {
                    accessToken = StringUtils.EMPTY;
                    log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"), jsonObject.getString("errmsg"));
                }
            }
            log.info("获取了微信AccessToken：{}", accessToken);
            return accessToken;
        } catch (Exception e) {
            log.error("请求失败" + e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getOpenId(String code){
        String openId = "";
        String getUrl = GET_USER_OPENID_AND_TOKEN + "&appid=" + appId + "&secret=" + appSecret + "&code=" + code;
        try {
            String result = HttpUtil.get(getUrl);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (null != jsonObject) {
                try {
                    openId = jsonObject.getString("openid");
                } catch (JSONException e) {
                    log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"), jsonObject.getString("errmsg"));
                }
            }
            log.info("获取了微信OpenId：{}", openId);
            return openId;
        } catch (Exception e) {
            log.error("请求失败" + e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String processRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";

            // xml请求解析
            Map<String, String> requestMap = MessageUtil.getInstance().parseXml(request);
            log.info("微信消息体：{}", requestMap);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");

            // 回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(System.currentTimeMillis());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                log.info("微信公众平台接收到事件：{}", msgType);
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respContent = "谢谢您的关注，您可以通过点击下方“扫码入群”来加入我们的社群了解更多币圈资讯和新闻，还有更多币圈大牛等着你哦(●'◡'●)！";
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    String eventKey = requestMap.get("EventKey");
                    log.info("微信公众平台接收到自定义菜单事件：eventType={},key={}", eventType, eventKey);
                    if (eventKey.equals("JOIN_US")) {
                        PicMessage picMessage = new PicMessage();
                        picMessage.setToUserName(fromUserName);
                        picMessage.setFromUserName(toUserName);
                        picMessage.setCreateTime(System.currentTimeMillis());
                        picMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
                        picMessage.setMediaId("6X_RduJsEvCbkeXlbtbMS61PHRQtMBRMf52oTpzBj8k");
                        return picMessage.toXMLMessag();
                    } else {
                        respContent = "点击了按钮";
                    }
                }
            }
            textMessage.setContent(respContent);
            respMessage = MessageUtil.getInstance().textMessageToXml(textMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respMessage;
    }
}

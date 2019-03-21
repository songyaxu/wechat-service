package com.usforever.wechatservice.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.usforever.wechatservice.common.AesException;
import com.usforever.wechatservice.common.Result;
import com.usforever.wechatservice.service.WeChatService;
import com.usforever.wechatservice.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 10:35 2018/4/11
 * @Modified by:
 */
@RestController
@Slf4j
@RequestMapping("wx")
public class WeChatController {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    @Value("${wechat.token}")
    private String token;

    @Autowired
    private WeChatService weChatService;

    private String getTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi";

    private String createMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?";

    private String menu = "{\"button\":[{\"type\":\"view\",\"name\":\"系列文章\",\"url\":\"https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzU2MzQ5NjY3NQ==#wechat_redirect\"},{\"type\":\"view\",\"name\":\"入群\",\"url\":\"http://mmbiz.qpic.cn/mmbiz_jpg/UFkcAicdHzKGU6P6AlmxI0V5UiaLrRqFyVCdibq1vMZfP7b2HEVoMJU1CaqhxiabH2GaYjwZ2lDKfXlibFZ0vxDGWWA/0\"}]}";

    @RequestMapping("jssdk")
    public Map<String, String> sign(String url, HttpSession session) {
        log.info("JSSDK请求参数URL:{}", url);
        String accessToken = "";
        String jsapi_ticket = "";
        //if(session.getAttribute("accessToken")==null)
        accessToken = weChatService.getAccessToken();
        if (accessToken != "") {
            String getUrl = getTicketUrl + "&access_token=" + accessToken;
            try {
                String result = HttpUtil.get(getUrl);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (null != jsonObject) {
                    try {
                        jsapi_ticket = jsonObject.getString("ticket");
                    } catch (JSONException e) {
                        // 获取jsapi_ticket失败
                        log.error("获取jsapi_ticket失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"), jsonObject.getString("errmsg"));
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("请求失败" + e.getMessage());
                return null;
            }
            Map<String, String> ret = new HashMap<String, String>();
            String nonce_str = create_nonce_str();
            String timestamp = create_timestamp();
            String string1;
            String signature = "";

            //注意这里参数名必须全部小写，且必须有序
            string1 = "jsapi_ticket=" + jsapi_ticket +
                    "&noncestr=" + nonce_str +
                    "&timestamp=" + timestamp +
                    "&url=" + url;
            log.info("微信JSSDK元素：{}", string1);

            try {
                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                crypt.reset();
                crypt.update(string1.getBytes("UTF-8"));
                signature = byteToHex(crypt.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret.put("appId", appId);
            ret.put("jsapi_ticket", jsapi_ticket);
            ret.put("nonceStr", nonce_str);
            ret.put("timestamp", timestamp);
            ret.put("signature", signature);

            return ret;
        } else {
            return null;
        }
    }

    @RequestMapping("getOpenId")
    public Result getOpenId(String code){
        String openId = weChatService.getOpenId(code);
        return Result.success(openId);
    }

    @RequestMapping("verification")
    public String weChatVerification(String signature, String timestamp, String nonce, String echostr, HttpServletRequest request) throws AesException {
        log.info("微信校验参数：signature={}，timestamp={}，nonce={}，echostr={}", signature, timestamp, nonce, echostr);
        if (echostr != null) {
            if (signature.equals(getSHA1(token, timestamp, nonce))) {
                return echostr;
            } else {
                return "";
            }
        } else {
            String res = weChatService.processRequest(request);
            log.info("微信消息回复体：{}", res);
            return res;
        }
    }

    @RequestMapping("createMenu")
    public String createMenu() {
        String accessToken = "";
        accessToken = weChatService.getAccessToken();
        if (StringUtils.isNotEmpty(accessToken)) {
            String url = createMenuUrl + "access_token=" + accessToken;
            try {
                JSONObject postJson = JSONObject.parseObject(menu);
                String result = HttpUtil.postJSON(url, postJson);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (null != jsonObject) {
                    try {
                        String code = jsonObject.getString("errcode");
                        if (code.equals(0)) {
                            return "创建微信公众号菜单成功！";
                        }
                        return "创建微信公众号菜单失败：errorMsg:" + jsonObject.getString("errmsg");
                    } catch (JSONException e) {
                        log.error("创建菜单失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"), jsonObject.getString("errmsg"));
                        return "创建失败";
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return "创建失败";
            }
        }
        return "创建失败";
    }

    @RequestMapping("getCurrentToken")
    private Result<String> getAccessToken() {
        String token =  weChatService.getAccessToken();
        return Result.success(token);
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private String getSHA1(String token, String timestamp, String nonce) throws AesException {
        try {
            String[] array = new String[]{token, timestamp, nonce};
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ComputeSignatureError);
        }
    }
}

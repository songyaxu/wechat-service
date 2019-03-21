package com.usforever.wechatservice.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 16:57 2018/4/16
 * @Modified by:
 */
public interface WeChatService {

    /**
     * 微信响应消息
     * @param request
     * @return
     */
    String processRequest(HttpServletRequest request);

    /**
     * 获取Access_token
     * @return
     */
    String getAccessToken();

    /**
     * 获取用户openId
     * @param code
     * @return
     */
    String getOpenId(String code);
}

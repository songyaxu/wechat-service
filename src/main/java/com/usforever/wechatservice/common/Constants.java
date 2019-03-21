package com.usforever.wechatservice.common;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 13:05 2019/2/18
 * @Modified by:
 */
public class Constants {

    public static final String GET_ACCESS_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    public static final String GET_USER_OPENID_AND_TOKEN="https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";

    public static final Integer EXPIRE_TIME = 60 * 24 * 30;
}

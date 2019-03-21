package com.usforever.wechatservice.bo;

import lombok.Data;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 17:09 2018/4/16
 * @Modified by:
 */
@Data
public class TextMessage {
    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
    private String Content;
}

package com.usforever.wechatservice.bo;

import lombok.Data;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 18:04 2018/4/16
 * @Modified by:
 */
@Data
public class PicMessage {
    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
    private String MediaId;

    public String toXMLMessag(){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<ToUserName>");
        sb.append("<![CDATA["+ToUserName+"]]>");
        sb.append("</ToUserName>");
        sb.append("<FromUserName>");
        sb.append("<![CDATA["+FromUserName+"]]>");
        sb.append("</FromUserName>");
        sb.append("<CreateTime>");
        sb.append(CreateTime);
        sb.append("</CreateTime>");
        sb.append("<MsgType>");
        sb.append("<![CDATA["+MsgType+"]]>");
        sb.append("</MsgType>");
        sb.append("<Image>");
        sb.append("<MediaId>");
        sb.append("<![CDATA["+MediaId+"]]>");
        sb.append("</MediaId>");
        sb.append("</Image>");
        sb.append("</xml>");
        return sb.toString();
    }
}

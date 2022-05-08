package com.onetop.webadmin.util;

import javax.servlet.http.HttpSession;
import java.util.ResourceBundle;

public class MessageUtil {

    public String getMessage(HttpSession session, String key){
        String bundle = "";
        String localeMessage = "";

        String lang = session.getAttribute("language").toString();

        if (lang .indexOf("ko") != -1) {
            bundle = "i18n.messages_ko";
        } else {
            bundle = "i18n.messages_en";
        }
        try {

            ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle);
            if (lang.indexOf("ko") > -1) {
                //한글일 경우 UTF-8로 인코딩.
                localeMessage = new String(resourceBundle.getString(key).getBytes("8859_1"), "utf-8");
            } else {
                localeMessage = resourceBundle.getString(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return localeMessage;
    }
}

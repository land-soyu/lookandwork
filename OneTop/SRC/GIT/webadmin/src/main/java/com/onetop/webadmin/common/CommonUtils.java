package com.onetop.webadmin.common;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

import static org.codehaus.groovy.runtime.EncodingGroovyMethods.sha256;

public class CommonUtils {

    public static String createUserPassword(String id, String pw) {
        String hashPw = "";
        try {
            String encodePw = new String(Base64.getEncoder().encodeToString(id.getBytes("UTF-8"))) + pw;
            hashPw = sha256(encodePw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashPw;
    }

    public static String md5(String str){
        String MD5 = "";
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }

    public static Object getKey(HashMap<Integer, String> m, Object value) {
        for(Object o: m.keySet()) {
            if(m.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public static String getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        return localeResolver.resolveLocale(request).toString();
    }
}

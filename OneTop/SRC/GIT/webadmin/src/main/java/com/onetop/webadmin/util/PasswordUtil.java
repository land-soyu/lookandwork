package com.onetop.webadmin.util;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String sha256(String base) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {

                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex) {

            throw new RuntimeException(ex);
        }
    }

    /**
     * 사용자 패스워드를 생성한다.
     * @param id
     * @param pw
     * @return
     */
    public static String createUserPassword(String id, String pw) throws Exception {

        String hashPw = "";

        try {

            String encodePw = new String(Base64.encodeBase64(id.getBytes("UTF-8"))) + pw;
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

    /**
     * 사용자의 mbKey 를 생성한다.
     */
    public static String createUserMbKey(String id, String pw) throws Exception {
        String hash = "";
        try {
            String encodePw = new String(Base64.encodeBase64(id.getBytes("UTF-8"))) + pw;
            hash = md5(sha256(encodePw));
        } catch (Exception e) {
            e.printStackTrace();            
        }
        return hash;
    }

    /**
     * 사용자의 패스워드를 비교한다.
     * @param orgPassword
     * @param rawPassword
     * @param userId
     * @return
     */
    public static boolean matchesPassword(String orgPassword, String rawPassword, String userId) {

        try {

            String encodePw = new String(Base64.encodeBase64(userId.getBytes("UTF-8"))) + rawPassword;
            if(orgPassword.equals(sha256(encodePw))) {

                return true;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return false;
    }
}

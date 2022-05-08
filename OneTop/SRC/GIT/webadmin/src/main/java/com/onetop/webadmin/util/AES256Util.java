package com.onetop.webadmin.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AES256Util {

    private String iv;
    private Key keySpec;

    public AES256Util(String source1, String source2) throws UnsupportedEncodingException {

        String source = source1 + source2;

        byte[] keyBytes = new byte[32];
        byte[] b = source.getBytes("UTF-8");
        int len = b.length;

        // source1 이 32 byte 초과시
        if(len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);

        this.keySpec = new SecretKeySpec(keyBytes, "AES");
        this.iv = source.substring(0, 16);
    }

    public String aesEncode(String plainText) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException
            , NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeBase64(encrypted));

        return enStr;
    }

    public String aesDecode(String decText) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException
            , NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

        byte[] byteStr = Base64.decodeBase64(decText.getBytes());

        return new String(cipher.doFinal(byteStr), "UTF-8");
    }
}
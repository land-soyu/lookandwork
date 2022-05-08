package com.onetop.webadmin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;

import com.onetop.webadmin.models.RSA;

import java.security.*;
import java.security.spec.RSAPublicKeySpec;

@Service
public class RSAUtil {

    private static final Logger log = LoggerFactory.getLogger(RSAUtil.class);

    private KeyPairGenerator keyPairGenerator;
    private KeyFactory keyFactory;
    private KeyPair keyPair;
    private Cipher cipher;

    public RSAUtil() {

        try {

            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            keyFactory = KeyFactory.getInstance("RSA");
            cipher = Cipher.getInstance("RSA");
        } catch (Exception e) {

            log.error("Fail to produce RSAUtil, msg = " + e.getMessage());
        }
    }

    /**
     * 새로운 키값을 가진 RSA 생성
     * @return
     */
    public RSA createRSA() {

        RSA rsa = null;

        try {

            keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            String modulus = publicKeySpec.getModulus().toString(16);
            String exponent = publicKeySpec.getPublicExponent().toString(16);
            rsa = new RSA(privateKey, modulus, exponent);
        } catch (Exception e) {

            log.error("Fail to create RSA, msg = " + e.getMessage());
        }

        return rsa;
    }

    /**
     * 개인키를 이용한 RSA 복호화
     * @param privateKey
     * @param password
     * @return
     * @throws Exception
     */
    public String getDecryptPassword(PrivateKey privateKey, String password) throws Exception{

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher.doFinal(hexToByteArray(password));

        return new String(decryptedBytes, "UTF-8");
    }

    /**
     * 16진수 문자열을 byte 배열로 변환
     * @param hex
     * @return
     */
    private byte[] hexToByteArray(String hex) {

        if (hex == null || hex.length() % 2 != 0) {

            return new byte[] {};
        }

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {

            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }

        return bytes;
    }
}
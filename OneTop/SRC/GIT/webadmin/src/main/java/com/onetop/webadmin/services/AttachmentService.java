package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.onetop.webadmin.config.ServerConfigs;
import com.onetop.webadmin.util.AES256Util;
import com.onetop.webadmin.util.PasswordUtil;

@Service
public class AttachmentService {

    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);



    /**
     * 문의/답변 파일 경로
     */
    private static final String QNA_FILE_DIR = "/ext-data";


    /**
     * NOTICE, QNA-COMMENT 이미지 파일 경로
     */
    private static final String FILE_DIR = "/ext-data/image";


    /**
     * QNA-COMMENT, ADMIN_QNA, ADMIN_QNA_COMMENT 파일 경로
     */
    private static final String ADMIN_FILE_DIR = "/app/data";


    /**
     * 첨부파일 삭제
     */
    public void deleteFile(String base_dir, String pageUrl) throws Exception {
        String filePath = base_dir+ File.separator;

        File file = new File(filePath);

        if (file.exists()) {

            File[] tempFile = file.listFiles();

            if (tempFile.length > 0) {

                for (int i = 0; i < tempFile.length; i++) {

                    if (tempFile[i].isFile()) {

                        tempFile[i].delete();
                    } else {

                        deleteFile(tempFile[i].getPath(), pageUrl);
                    }

                    tempFile[i].delete();
                }

                file.delete();
            }
        }
    }


    /**
     * AES256 암호화된 이미지 읽어오기
     */
    public String getAES256EncodeImage(String base_dir, String attach_name) {

        String result = "";

        String fileName = base_dir + "/" + attach_name;
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String text = sb.toString();

            AES256Util aes256Util = new AES256Util(PasswordUtil.sha256(ServerConfigs.DEPOSIT_KRW_HIGH_SHA256_KEY), "");
            result = aes256Util.aesDecode(text);

        } catch (Exception e) {
            log.error("Fail to get image, msg =" + e.getMessage());
        }
        return result;
    }
}
package com.onetop.webadmin.util;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String GetDateStringFromFormat(Date date, String formatString){
        String dateString = "";
        try{
            SimpleDateFormat transFormat = new SimpleDateFormat(formatString);
            dateString = transFormat.format(date);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return dateString;
    }

    public static HashMap<String, String> SendHttpRequest(String url, JSONObject params, String language){
        HashMap<String, String> resultSet = new HashMap<>();

        try{
            if(url == null) throw new Exception("url is null");
            if(params == null) params = new JSONObject();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

            List<Locale.LanguageRange> languageRanges = new ArrayList<>();

            if(language.equals("ko")){
                languageRanges.add(new Locale.LanguageRange("ko"));
            }
            else{
                languageRanges.add(new Locale.LanguageRange("en"));
            }

            headers.setAcceptLanguage(languageRanges);

            HttpEntity param = new HttpEntity(params.toString(), headers);

            String returnStr = restTemplate.postForObject(url, param, String.class);
            resultSet.put("responseCode", "success");
            resultSet.put("resultData", returnStr);

            log.info(String.format("http request. url : [%s], language : [%s], params : [%s], result : [%s]", url, language, params.toJSONString(), returnStr));
        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("responseCode", "fail");
            resultSet.put("resultData", null);
            log.info(e.toString());
        }

        return resultSet;
    }

    public static Date GetDateAddDays(int addDay){
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, addDay);
        return cal.getTime();
    }

    public static Date GetDateAddDays(Date date, int addDay){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, addDay);
        return cal.getTime();
    }

    public static Date GetDateAddMonths(Date date, int addMonth){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.MONTH, addMonth);
        return cal.getTime();
    }

    public static Date GetDateAddYears(Date date, int addYear){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.YEAR, addYear);
        return cal.getTime();
    }

    public static Date GetDateAddHours(Date date, int addHour){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.HOUR, addHour);
        return cal.getTime();
    }

    public static String padRight(String s, char appendChar, int len){
        if(s.length() < len){
            StringBuilder sb = new StringBuilder(s);
            for(int i = len - s.length(); i > 0; i--){
                sb.append(appendChar);
            }

            return sb.toString();
        }
        return s;
    }

    public static String padLeft(String s, char appendChar, int len){
        if(s.length() < len){
            StringBuilder sb = new StringBuilder();
            for(int i = len - s.length(); i > 0; i--){
                sb.append(appendChar);
            }
            sb.append(s);

            return sb.toString();
        }
        return  s;
    }

    public static String GetWhereString(List<String> conditionList){
        String where = "";

        if (conditionList.size() > 0) {
            where += " WHERE ";

            for (int i = 0; i < conditionList.size(); i++) {
                if (i == 0) {
                    where += conditionList.get(i);
                } else {
                    where += (" AND " + conditionList.get(i));
                }
            }
        }

        return where;
    }

    public static Date GetDateFromStringFormat(String dateString, String format){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateString);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public  static String GetDateStringAddDays(String dateString, String format, int addDay){
        return GetDateStringFromFormat(GetDateAddDays(GetDateFromStringFormat(dateString, format), addDay), format);
    }

    public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.########");

    public static String GetClosedImageTag(String content){
        Pattern nonValidPattern = Pattern
                .compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");

        Matcher matcher = nonValidPattern.matcher(content);
        while (matcher.find()) {
            String oldTag = matcher.group(0);
            if(!oldTag.endsWith("/>")){
                String newTag = oldTag.replace(">" , "/>");
                content = content.replace(oldTag, newTag);
            }
        }

        return content;
    }

    public static String GetSubString(String content, int maxLength){
        if(content != null && content.length() > maxLength){
            content = content.substring(0, maxLength - 1);
        }

        return content;
    }

    /**
     * UTC -> KST 변환
     * @param date
     * @return
     */
    public static Date ConvertUTCtoKST(Date date) {
        return date;
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = null;

        try {

            resultDate = GetDateAddHours(sdf.parse(sdf.format(date)) , 9);
        } catch (Exception e) {

            log.error("Fail to convert utc to kst, msg = " + e.getMessage());
        }

        return resultDate;
        */
    }
}

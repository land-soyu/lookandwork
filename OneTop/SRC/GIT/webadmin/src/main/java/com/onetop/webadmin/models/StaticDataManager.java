package com.onetop.webadmin.models;

import java.util.Date;
import java.util.HashMap;

public class StaticDataManager {
    private HashMap<String, StaticData> staticDataHashMap;

    public StaticDataManager(){
        this.staticDataHashMap = new HashMap<>();
    }

    private synchronized Boolean IsValidStaticData(String staticDataTag, int validPeriodSec){
        try{
            if(this.staticDataHashMap == null) this.staticDataHashMap = new HashMap<>();
            if(!this.staticDataHashMap.containsKey(staticDataTag)) return false;
            if(this.staticDataHashMap.get(staticDataTag) == null) return false;
            if(this.staticDataHashMap.get(staticDataTag).getUpdatedDt() == null) return false;
            if(this.staticDataHashMap.get(staticDataTag).getData() == null) return false;
            // 날짜 validation
            Date updatedDt = this.staticDataHashMap.get(staticDataTag).getUpdatedDt();
            Date now = new Date();
            long elapsedMills = now.getTime() - updatedDt.getTime();
            if(elapsedMills / 1000 >= validPeriodSec) return false;

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void UpdateStaticData(String staticDataTag, Object data){
        try {
            if (this.staticDataHashMap == null) this.staticDataHashMap = new HashMap<>();
            this.staticDataHashMap.put(staticDataTag, new StaticData(new Date(), data));

            System.out.println(String.format("%s is updated.", staticDataTag));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized Object GetStaticData(String staticDataTag, int validPeriodSec){
        try{
            if(IsValidStaticData(staticDataTag, validPeriodSec)){
                return this.staticDataHashMap.get(staticDataTag).getData();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private class StaticData{
        private Date updatedDt;
        private Object data;

        public StaticData(Date updatedDt, Object data){
            this.updatedDt = updatedDt;
            this.data = data;
        }

        public Date getUpdatedDt() {
            return updatedDt;
        }

        public Object getData() {
            return data;
        }
    }
}
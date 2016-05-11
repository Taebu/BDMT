package com.anp.ulsanfood.gcm;

import java.util.*;

public class Dictionary {

    public static final int	DATA_STRING			= 0;
    public static final int	DATA_INTEGER		= 1;
    public static final int	DATA_ARRAYLIST_DIC	= 2;

    private Map<String,Object> 	map;
    private Map<String,Integer> dataTypeMap;

    public Dictionary() {
        map = new HashMap<String,Object>();
        dataTypeMap = new HashMap<String,Integer>();
    }

    public Dictionary(String[] key, String value[]) {
        map = new HashMap<String,Object>();
        dataTypeMap = new HashMap<String,Integer>();

        for(int i=0;i<key.length;i++) {
            map.put(key[i],value[i]);
            dataTypeMap.put(key[i],DATA_STRING);
        }
    }

    public Dictionary(String[] key, Integer value[]) {
        map = new HashMap<String,Object>();
        dataTypeMap = new HashMap<String,Integer>();

        for(int i=0;i<key.length;i++) {
            map.put(key[i],value[i]);
            dataTypeMap.put(key[i],DATA_INTEGER);
        }
    }

    public Dictionary(String[] key, Object value[], int type[]) {
        map = new HashMap<String,Object>();
        dataTypeMap = new HashMap<String,Integer>();

        for(int i=0;i<key.length;i++) {
            map.put(key[i],value[i]);
            dataTypeMap.put(key[i],type[i]);
        }
    }

    public void addString(String key, String value) {
        map.put(key,value);
    }

    public String getString(String key) {
        return (String)map.get(key);
    }

    public void addObject(String key, Object value) {
        map.put(key,value);
    }

    public Object getObject(String key) {
        return map.get(key);
    }

    public void setType(String key, int type) {
        dataTypeMap.put(key,type);
    }

    public int getType(String key) {
        return (int)dataTypeMap.get(key);
    }

    public int getSize() {
        return map.size();
    }
}

package com.stone.ct.common.constant;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfigConstant {

    private static Map<String, String> valueMap = new HashMap<>();

    static {
        ResourceBundle ct = ResourceBundle.getBundle("ct");
        Enumeration<String> keys = ct.getKeys();
        while (keys.hasMoreElements()){
            String key = keys.nextElement();
            String value = ct.getString(key);
            valueMap.put(key,value);
        }
    }

    public static String getVal(String key){
        return valueMap.get(key);
    }

    public static void main(String[] args) {
        System.out.println(ConfigConstant.getVal("ct.cf.caller"));
    }

}

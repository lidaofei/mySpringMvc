package com.feixiang.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lidaofei
 * @Date: 2019/3/18 0:43
 */
public class ConverterMap {
    private static Map<String,Class<?>> converterMap = new HashMap<>();
    public static void setConverterMap(String key,Class clazz){
        converterMap.put(key,clazz);
    }
    public static Class<?> getConerterClazz(String key){
        return converterMap.get(key);
    }
}

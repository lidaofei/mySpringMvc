package com.feixiang.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lidaofei
 * @Date: 2019/3/17 20:58
 */
public class RequestMappingMap {
    private static Map<String,Class> reqMappingMap = new HashMap<String, Class>();
    public static  Map<String,Class> getReqMappingMap(){
        return reqMappingMap;
    }

    public static void setReqMappingMap(String mappingValue,Class clazz){
        reqMappingMap.put(mappingValue,clazz);
    }

    public static Class getClazz(String mappingValue){
        return reqMappingMap.get(mappingValue);
    }


}

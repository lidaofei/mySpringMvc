package com.feixiang.converter;

/**
 * @Author: lidaofei
 * @Date: 2019/3/18 0:38
 */
public class LongConverter implements ConverterFactory {
    @Override
    public Object converter(String origin) {
        return Long.valueOf(origin);
    }
}

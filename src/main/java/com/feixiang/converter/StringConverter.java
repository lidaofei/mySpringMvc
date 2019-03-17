package com.feixiang.converter;

/**
 * @Author: lidaofei
 * @Date: 2019/3/18 0:38
 */
public class StringConverter implements ConverterFactory {
    @Override
    public Object converter(String origin) {
        return origin;
    }
}

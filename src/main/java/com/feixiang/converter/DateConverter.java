package com.feixiang.converter;

import java.util.Date;

/**
 * @Author: lidaofei
 * @Date: 2019/3/18 0:38
 */
public class DateConverter implements ConverterFactory {
    @Override
    public Object converter(String origin) {
        return new Date(Long.valueOf(origin)*1000L);
    }
}

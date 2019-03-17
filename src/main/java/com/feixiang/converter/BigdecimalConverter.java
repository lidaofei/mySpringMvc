package com.feixiang.converter;

import java.math.BigDecimal;

/**
 * @Author: lidaofei
 * @Date: 2019/3/18 0:38
 */
public class BigdecimalConverter implements ConverterFactory {
    @Override
    public Object converter(String origin) {
        return new BigDecimal(origin).setScale(2,BigDecimal.ROUND_HALF_DOWN);
    }
}

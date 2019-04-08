package com.stone.ct.common.util;

import java.text.DecimalFormat;

/**
 * 数字工具类
 */
public class NumberUtil {

    /**
     * 将数字格式化字符串
     * @param num
     * @param length
     * @return
     */
    public static String format(int num, int length){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= length; i++) {
            stringBuilder.append("0");
        }
        DecimalFormat df = new DecimalFormat(stringBuilder.toString());
        return df.format(num);
    }

}

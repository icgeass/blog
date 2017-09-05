package com.zeroq6.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class MyMoneyUtils {

    public static String format(BigDecimal bigDecimal, boolean flag) {
        if (null == bigDecimal) {
            return "0.00";
        }
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setGroupingUsed(flag);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setMaximumIntegerDigits(12);
        return nf.format(bigDecimal);
    }


}

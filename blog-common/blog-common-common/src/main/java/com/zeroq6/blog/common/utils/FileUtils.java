package com.zeroq6.blog.common.utils;

import java.math.BigDecimal;

/**
 * Created by yuuki asuna on 2017/8/10.
 */
public class FileUtils {

    public static String getReadableSize(long fileSizeInBytes) {
        int i = 0;
        BigDecimal result = new BigDecimal(fileSizeInBytes);
        BigDecimal Digit1024 = new BigDecimal(1024);
        String[] byteUnits = new String[]{ " Bytes", " KiB", " MiB", " GiB", " TiB", " PiB", " EiB",
                " ZiB", " YiB" };
        while (result.compareTo(Digit1024) >= 0) {
            result = result.divide(Digit1024); // 一定能除尽
            i++;
        }
        return result.setScale(2, BigDecimal.ROUND_DOWN).toString() + byteUnits[i];
    }

}



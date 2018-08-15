package com.zeroq6.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author
 * @date 2018/8/15
 */
public class ExceptionUtils {


    /**
     * 将 Exception 转化为 String
     */
    public static String getExceptionToString(Throwable e) {
        if (e == null){
            return "null";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}

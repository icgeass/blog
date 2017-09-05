package com.zeroq6.common.utils;

import org.apache.commons.lang3.StringUtils;
import java.util.Enumeration;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
/**
 * 取这个名字是因为还没见到三方框架用这个名字
 */
public class MyStringUtils {


    public static String format(String template, Object[] objects) {
        String re = String.valueOf(template); // if null then "null";
        if(null != objects){
            for (int i = 0; i < objects.length; i++) {
                re = re.replace("{" + i + "}", null == objects[i] ? "null" : objects[i].toString());
            }
        }
        return re;
    }


    public static int findSubStringTimes(String str, String findStr) {
        if (null == str || null == findStr) {
            throw new RuntimeException("str和findStr不能为null");
        }
        int lastIndex = 0;
        int count = 0;
        int findStrLength = findStr.length();
        while ((lastIndex = str.indexOf(findStr, lastIndex)) != -1) {
            count++;
            lastIndex += findStrLength;
        }
        return count;
    }


    public static String removeInner(String string, String prefix, String suffix){
        if(null == string){
            return null;
        }
        if(null == prefix || null == suffix){
            throw new RuntimeException("prefix and suffix can not be null");
        }
        StringBuffer sb = new StringBuffer(string);
        int beginIndex = -1;
        int endIndex = -1;
        while ((beginIndex = sb.indexOf(prefix)) != -1){
            if((endIndex = sb.indexOf(suffix, beginIndex)) != -1){
                sb.delete(beginIndex, endIndex + suffix.length());
            }else{
                break;
            }
        }
        return sb.toString();
    }

    public static String[] toStringArray(Object obj){
        if(null == obj){
            throw new RuntimeException("obj can not be null");
        }
        if(obj instanceof String[]){
            return (String[])obj;
        }
        if(obj instanceof String){
            return new String[]{String.valueOf(obj)};
        }
        throw new RuntimeException("obj can not convert to string array");
    }


}

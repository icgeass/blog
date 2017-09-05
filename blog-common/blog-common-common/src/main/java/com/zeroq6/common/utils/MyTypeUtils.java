package com.zeroq6.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class MyTypeUtils {

    public static <T> T transfer(Object source, TypeReference<T> typeReference){
        String json = JSON.toJSONString(source);
        Object obj = JSON.parseObject(json, typeReference);
        return (T)obj;
    }


}
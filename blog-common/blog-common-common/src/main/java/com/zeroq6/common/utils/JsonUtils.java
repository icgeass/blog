package com.zeroq6.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * https://blog.csdn.net/stubbornness1219/article/details/52947013
 * <p>
 * http://janle.iteye.com/blog/2392230
 */
public class JsonUtils {

    private final static List<String> MASK_ALL_FIELD_INNER = new ArrayList<String>() {
        {
            add("pass");
            add("pwd");
        }
    };

    private final static Pattern SENSITIVE_STRING_PATTERN = Pattern.compile("^\\d+[xX]?$");


    /**
     * ValueFilter可改变value值，PropertyFilter只能决定字段序列化或不序列化
     *
     * 如果返回null，由于fastjson默认不打印null，则不会打印
     */
    private final static ValueFilter valueFilter = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            if (null == value || value instanceof byte[] || value instanceof Byte[]) {
                return null;
            }
            if (!(value instanceof String)) {
                return value;
            }
            for (String ignore : MASK_ALL_FIELD_INNER) {
                if (name.toLowerCase().contains(ignore)) { // 宁可错杀
                    return "******";
                }
            }
            String stringValue = String.valueOf(value);
            if (StringUtils.isNotBlank(stringValue)) {
                int len = stringValue.length();
                // 手机号，身份证号，银行卡
                if (len == 11 || len == 18 || len == 16 || len == 19) {
                    if (SENSITIVE_STRING_PATTERN.matcher(stringValue).matches()) {
                        // start（不含）之前 和 end（含）之后显示
                        return StringUtils.overlay(stringValue, StringUtils.repeat("*", len - 4), 2, len - 2);
                    }
                }
            }

            return value;
        }
    };

    public static String toJSONString(Object object, SerializerFeature... features) {
        return JSON.toJSONString(object, valueFilter, features);
    }


}
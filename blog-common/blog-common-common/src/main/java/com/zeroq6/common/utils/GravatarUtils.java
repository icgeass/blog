package com.zeroq6.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yuuki asuna on 2017/7/10.
 */
public class GravatarUtils {

    private final static String DEFAULT_GRAVATAR_URL = "https://www.gravatar.com/avatar/00000000000000000000000000000000?s=125";

    private final static Map<String, String> GRAVATAR_URL_CACHE = Collections.synchronizedMap(new LinkedHashMap(10, 0.75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 1000;
        }
    });


    public static String getAvatar(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            return DEFAULT_GRAVATAR_URL;
        }
        StringBuffer sb = new StringBuffer("https://www.gravatar.com/avatar/");
        sb.append(DigestUtils.md5Hex(email));
        sb.append("?s=125");
        GRAVATAR_URL_CACHE.put(email, sb.toString());
        return GRAVATAR_URL_CACHE.get(email);
    }
}

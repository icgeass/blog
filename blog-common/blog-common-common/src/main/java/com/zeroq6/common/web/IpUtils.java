package com.zeroq6.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by icgeass on 2017/2/22.
 */

public class IpUtils {


    private final static Logger logger = LoggerFactory.getLogger(IpUtils.class);


    // http://zhangxugg-163-com.iteye.com/blog/1663687
    // 切记，$_SERVER['REMOTE_ADDR']  是由 nginx 传递给 php 的参数，就代表了与当前 nginx 直接通信的客户端的 IP （是不能伪造的）。
    // REMOTE_ADDR放在最前面
    private final static String[] HEADERS_IP_TO_TRY = {
            "X-Real-IP",
            "REMOTE_ADDR",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA"
    };

    /**
     * 得到客户端ip
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        for (int i = 0; i < HEADERS_IP_TO_TRY.length; i++) {
            ip = request.getHeader(HEADERS_IP_TO_TRY[i]);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    try {
                        ip = ip.split("\\s*,\\s*")[0].trim();
                    } catch (Exception e) {
                        ip = ip.replaceAll("\\s*,\\s*", " ");
                    }
                }
                return ip;
            }
        }
        ip = request.getRemoteAddr();
        return ip;
    }


}

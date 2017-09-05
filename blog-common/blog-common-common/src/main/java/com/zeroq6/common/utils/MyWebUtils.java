package com.zeroq6.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class MyWebUtils {

    private static final String[] HEADERS_IP_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };


    /**
     * 得到客户端ip
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        for (String header : HEADERS_IP_TO_TRY) {
            ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                if(ip.contains(",")){
                    try {
                        ip = ip.split("[,]")[0].trim();
                    }catch (Exception e){
                        // ignore
                    }
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }


    /**
     * 等到服务端ip
     * @return
     * @throws Exception
     */
    public static String getServerIp() throws Exception {
        String localIp = "127.0.0.1";
        String netIp = null;
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":" ) == -1) {
                    if(!ip.isSiteLocalAddress()){
                        netIp =  ip.getHostAddress();
                        break;
                    }else{
                        localIp = ip.getHostAddress();
                    }
                }
            }
        }
        if(null != netIp && netIp.trim().length() != 0){
            return netIp;
        }else{
            return localIp;
        }

    }


    public static String getReturnUrl(String prefix, HttpServletRequest request) throws Exception{
        StringBuilder parameters = null;
        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            String name = e.nextElement();
            parameters = new StringBuilder();
            parameters.append('?').append(name).append('=').append(request.getParameter(name));
        }
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            parameters.append('&').append(name).append('=').append(request.getParameter(name));
        }
        return prefix + java.net.URLEncoder.encode(request.getRequestURL().append(parameters != null ? parameters : "").toString(), "GBK");
    }

}

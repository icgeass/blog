package com.zeroq6.common.web;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestUtils {


    /**
     * 将当前请求链接，包括参数用URLEncoder 指定charset，编码并返回
     *
     * @param request
     * @param ignoreParameterNames
     * @return
     * @throws Exception
     */
    public static String getQueryString(HttpServletRequest request, String... ignoreParameterNames) throws Exception {
        // 传参
        StringBuffer parameters = null;
        // 忽略参数名，传到登录页，不能有ticket的参数名
        List<String> ignoreList = new ArrayList<String>();
        if (null != ignoreParameterNames && ignoreParameterNames.length > 0) {
            for (String string : ignoreParameterNames) {
                ignoreList.add(string);
            }
        }
        // 处理参数
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            List<String> params = Arrays.asList(queryString.split("&"));
            boolean first = true;
            for (String param : params) {
                String[] kv = param.split("=");
                if (kv.length == 2 && !ignoreList.contains(kv[0])) {
                    if (first) {
                        parameters = new StringBuffer();
                        parameters.append('?').append(kv[0]).append('=').append(kv[1]);
                        first = false;
                    } else {
                        parameters.append('&').append(kv[0]).append('=').append(kv[1]);
                    }
                }
            }
        }
        return parameters != null ? parameters.toString() : null;
    }
}

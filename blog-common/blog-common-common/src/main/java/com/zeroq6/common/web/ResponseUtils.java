package com.zeroq6.common.web;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ResponseUtils {

    public static void outString(HttpServletResponse response, String responseBody) throws Exception {
        PrintWriter pw = response.getWriter();
        pw.write(responseBody);
        pw.close();
    }


    public static void doRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws Exception {
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", redirectUrl);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("ReturnUrl", redirectUrl);
            outString(response, jsonObject.toJSONString());
        } else {
            response.sendRedirect(redirectUrl);
        }
    }
}

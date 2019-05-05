package com.zeroq6.common.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static String get(HttpServletRequest req, String key) {
        Cookie cookie = getCookie(req, key);
        if (null == cookie) {
            return null;
        }
        return cookie.getValue();
    }


    public static void set(HttpServletResponse resp, Cookie cookie) {

        resp.addCookie(cookie);
    }


    public static void delete(HttpServletRequest req, HttpServletResponse resp, String key, String domain, String path) {
        Cookie cookie = getCookie(req, key);
        if (null != cookie) {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            if (null != domain && domain.trim().length() != 0) {
                cookie.setDomain(domain);
            }
            if (null != path && path.trim().length() != 0) {
                cookie.setPath(path);
            }
            resp.addCookie(cookie);
        }

    }

    public static Cookie getCookie(HttpServletRequest req, String key) {
        Cookie[] cookies = req.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

}

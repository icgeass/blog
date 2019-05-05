package com.zeroq6.blog.operate.web.interceptor;

import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.web.CookieUtils;
import com.zeroq6.common.web.IpUtils;
import com.zeroq6.common.web.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class LoginInterceptor implements HandlerInterceptor {


    @Autowired
    private LoginService loginService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${login.cookie.name}")
    private String loginCookieName = "";

    @Value("${login.uri.prefix}")
    private String loginUriPrefix;


    @Value("${login.cookie.path}")
    private String loginCookiePath;


    @Value("${login.cookie.domain}")
    private String loginCookieDomain;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        try {
            String uri = httpServletRequest.getRequestURI();
            if (uri.startsWith(loginUriPrefix)) {
                Cookie cookie = CookieUtils.getCookie(httpServletRequest, loginCookieName);
                if (null == cookie) {
                    ResponseUtils.doRedirect(httpServletRequest, httpServletResponse, "/auth/login");
                    return false;
                }
                boolean success = loginService.validateAndSetCurrUserLoginInfo(cookie.getValue(), IpUtils.getClientIp(httpServletRequest));
                if (!success) {
                    CookieUtils.delete(httpServletRequest, httpServletResponse, loginCookieName, loginCookieDomain, loginCookiePath);
                    ResponseUtils.doRedirect(httpServletRequest, httpServletResponse, "/auth/login");
                    return false;
                }

            }
            return true;
        } catch (Exception e) {
            logger.error("拦截器preHandle异常", e);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        try {
            loginService.removeCurrUserLoginInfo();
        } catch (Exception e1) {
            logger.error("拦截器afterCompletion异常", e1);
        }
    }



}

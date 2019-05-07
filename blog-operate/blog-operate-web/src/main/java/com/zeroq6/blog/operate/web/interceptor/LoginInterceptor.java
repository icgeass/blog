package com.zeroq6.blog.operate.web.interceptor;

import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.web.CookieUtils;
import com.zeroq6.common.web.IpUtils;
import com.zeroq6.common.web.RequestUtils;
import com.zeroq6.common.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


@Service
public class LoginInterceptor implements HandlerInterceptor, InitializingBean {


    @Autowired
    private LoginService loginService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${login.cookie.name}")
    private String loginCookieName = "";


    @Value("${login.cookie.path}")
    private String loginCookiePath;


    @Value("${login.cookie.domain}")
    private String loginCookieDomain;


    @Value("${login.return.url.name}")
    private String loginReturnUrlName;

    private String disallowUriPrefix;


    private final List<String> disallowUriPrefixList = new ArrayList<String>();


    public void setDisallowUriPrefix(String disallowUriPrefix) {
        this.disallowUriPrefix = disallowUriPrefix;
    }


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        try {
            String uri = httpServletRequest.getRequestURI();
            boolean intercept = false;
            for (String disallow : disallowUriPrefixList) {
                if (uri.startsWith(disallow)) {
                    intercept = true;
                }
            }
            if (intercept) {
                Cookie cookie = CookieUtils.getCookie(httpServletRequest, loginCookieName);
                if (null == cookie) {
                    ResponseUtils.doRedirect(httpServletRequest, httpServletResponse, getReturnUrl(httpServletRequest));
                    return false;
                }
                boolean success = loginService.validateAndSetCurrUserLoginInfo(cookie.getValue(), IpUtils.getClientIp(httpServletRequest));
                if (!success) {
                    CookieUtils.delete(httpServletRequest, httpServletResponse, loginCookieName, loginCookieDomain, loginCookiePath);
                    ResponseUtils.doRedirect(httpServletRequest, httpServletResponse, getReturnUrl(httpServletRequest));
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

    private String getReturnUrl(HttpServletRequest request) throws Exception {
        //
        String redirectUrl = "/auth/login/";
        if ("GET".equals(request.getMethod())) {
            //
            String queryString = RequestUtils.getQueryString(request, loginReturnUrlName);
            String returnUrl = request.getRequestURL() + (null != queryString ? queryString : "");
            returnUrl = Base64.getUrlEncoder().encodeToString(returnUrl.getBytes("UTF-8"));
            redirectUrl = redirectUrl + "?" + loginReturnUrlName + "=" + returnUrl;

        }
        return redirectUrl;


    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // disallow的uri
        if (StringUtils.isNotBlank(disallowUriPrefix)) {
            disallowUriPrefixList.addAll(Arrays.asList(disallowUriPrefix.split("\\s*,\\s*")));
        }
        logger.info("拦截器初始化完成");
    }
}

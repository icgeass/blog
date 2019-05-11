package com.zeroq6.blog.operate.web.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.web.CookieUtils;
import com.zeroq6.common.web.IpUtils;
import com.zeroq6.common.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Controller
@RequestMapping("/auth")
public class LoginController {


    @Autowired
    private LoginService loginService;


    @Value("${login.cookie.name}")
    private String loginCookieName = "";


    @Value("${login.cookie.path}")
    private String loginCookiePath;


    @Value("${login.cookie.domain}")
    private String loginCookieDomain;


    @Value("${login.return.url.name}")
    private String loginReturnUrlName;


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response, Model view) {
        String re = "/login";
        view.addAttribute("success", false);
        try {
            String clientCookieValue = CookieUtils.get(request, loginCookieName);
            if (loginService.validateLogin(clientCookieValue, IpUtils.getClientIp(request), false)) {
                ResponseUtils.doRedirect(request, response, "/admin");
            } else if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                String genCookieValue = loginService.login(username, password, IpUtils.getClientIp(request));
                if (StringUtils.isNotBlank(genCookieValue)) {
                    Cookie cookie = new Cookie(loginCookieName, genCookieValue);
                    cookie.setDomain(loginCookieDomain);
                    cookie.setPath(loginCookiePath);
                    cookie.setMaxAge(6 * 60 * 60);
                    cookie.setHttpOnly(true);
                    CookieUtils.set(response, cookie);
                    ResponseUtils.doRedirect(request, response, getRedirectUrl(request));
                    return null;
                } else {
                    addMessage(request, response, false, "用户名或密码错误", view);
                }
            }

        } catch (Exception e) {
            addMessage(request, response, false, "系统繁忙, 请稍后再试", view);
            logger.error("登录异常", e);
        }
        return re;
    }


    @RequestMapping(value = "/logout")
    public String logout(String username, String password, HttpServletRequest request, HttpServletResponse response, Model view) {
        String re = "/login";
        try {
            loginService.logout(CookieUtils.get(request, loginCookieName));
            ResponseUtils.doRedirect(request, response, "/");
            return null;
        } catch (Exception e) {
            view.addAttribute("message", "系统繁忙, 请稍后再试!");
            logger.error("登出异常", e);
        }
        return re;
    }


    private void addMessage(HttpServletRequest request, HttpServletResponse response, boolean success, String message, Model view) {
        try {
            if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", success);
                jsonObject.put("message", message);
                ResponseUtils.outString(response, jsonObject.toJSONString());
            } else {
                view.addAttribute("success", success);
                view.addAttribute("message", message);
            }
        } catch (Exception e) {
            logger.error("addMessage异常", e);
        }
    }

    private String getRedirectUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        String redirectUrl = request.getParameter(loginReturnUrlName);
        if(StringUtils.isBlank(redirectUrl)){
            return "/admin";
        }
        redirectUrl = new String(Base64.getUrlDecoder().decode(redirectUrl), "UTF-8");
        return redirectUrl;
    }


}

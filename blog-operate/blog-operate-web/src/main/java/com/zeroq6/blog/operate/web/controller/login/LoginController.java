package com.zeroq6.blog.operate.web.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeroq6.blog.common.base.BaseResponseCode;
import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.counter.Counter;
import com.zeroq6.common.counter.CounterService;
import com.zeroq6.common.security.RsaCrypt;
import com.zeroq6.common.utils.JsonUtils;
import com.zeroq6.common.utils.MyWebUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private RsaCrypt rsaCrypt;


    @Value("${counter.type.login.ip}")
    private String counterTypeLoginIp;

    @Value("${counter.type.login.username}")
    private String counterTypeLoginUsername;


    @Autowired
    private CounterService counterService;


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response, Model view) {
        String re = "/login";
        view.addAttribute("success", false);
        view.addAttribute("pubKey", rsaCrypt.getPublicKeyBase64());
        view.addAttribute("serverTimeMillis", System.currentTimeMillis());

        try {
            String clientCookieValue = CookieUtils.get(request, loginCookieName);
            if (loginService.validateLogin(clientCookieValue, IpUtils.getClientIp(request), false)) {
                ResponseUtils.doRedirect(request, response, "/admin");
                return null;
            } else if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                // 登录错误次数验证
                Map<String, String> query = new HashMap<String, String>();
                query.put(counterTypeLoginIp, MyWebUtils.getClientIp(request));
                query.put(counterTypeLoginUsername, username);
                List<Counter> counterList = counterService.get(query);
                for (Counter counter : counterList) {
                    if (counter.isLock()) {
                        addMessage(request, response, false, counter.getMessage(), view);
                        logger.info("用户被锁定，counter={}", JsonUtils.toJSONString(counter, SerializerFeature.WriteDateUseDateFormat));
                        return re;
                    }
                }


                BaseResponseCode<String> result = loginService.login(username, password, IpUtils.getClientIp(request));
                String resultCode = result.getCode();
                if (BaseResponseCode.CODE_SUCCESS.equals(resultCode)) {
                    Cookie cookie = new Cookie(loginCookieName, result.getBody());
                    cookie.setDomain(loginCookieDomain);
                    cookie.setPath(loginCookiePath);
                    cookie.setMaxAge(6 * 60 * 60);
                    cookie.setHttpOnly(true);
                    CookieUtils.set(response, cookie);
                    ResponseUtils.doRedirect(request, response, getRedirectUrl(request));
                    counterService.updateSuccess(counterList);
                    return null;
                } else if (BaseResponseCode.CODE_EXCEPTION.equals(resultCode)) {
                    addMessage(request, response, false, "登录服务异常,请稍后重试", view);
                } else {
                    counterService.updateFailed(counterList);
                    String message = "未知错误";
                    if (LoginService.CODE_FAILED_LOGIN_USERNAME_OR_PASSWORD_BLANK.equals(resultCode)) {
                        message = "用户名和密码不能为空";
                    } else if (LoginService.CODE_FAILED_LOGIN_USERNAME_OR_PASSWORD_ERROR.equals(resultCode)) {
                        message = counterService.getMessage(counterList);
                    } else if (LoginService.CODE_FAILED_LOGIN_REPEAT.equals(resultCode)) {
                        message = "非法请求，重复登录";
                    } else if (LoginService.CODE_FAILED_LOGIN_EXPIRE.equals(resultCode)) {
                        message = "登录过期，请重试";
                    }
                    addMessage(request, response, false, message, view);
                }


            }
        } catch (Exception e) {
            addMessage(request, response, false, "系统繁忙，请稍后再试", view);
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
        if (StringUtils.isBlank(redirectUrl)) {
            return "/admin";
        }
        redirectUrl = new String(Base64.getUrlDecoder().decode(redirectUrl), "UTF-8");
        return redirectUrl;
    }


}

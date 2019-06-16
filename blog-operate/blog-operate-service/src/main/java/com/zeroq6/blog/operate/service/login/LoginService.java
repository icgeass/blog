package com.zeroq6.blog.operate.service.login;


import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.common.security.RsaCrypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService {

    private final static ThreadLocal<UserLoginInfo> CURRENT_LOGIN_USER = new ThreadLocal<UserLoginInfo>();

    public final static String PASS_SLAT = "_PASS_SLAT_";

    private final Integer MAX_SIZE = 10000;

    // 大于等于1
    private final Integer MAX_ONLINE_PER_USER = 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${login.cookie.name}")
    private String loginCookieName = "";

    @Autowired
    private RsaCrypt rsaCrypt;


    private Map<String, String> usernamePasswordMap = new ConcurrentHashMap<String, String>();

    public void setUsernamePasswordMap(Map<String, String> usernamePasswordMap) {
        this.usernamePasswordMap = usernamePasswordMap;
    }

    private final Map<String, UserLoginInfo> keyUserLoginInfoMap = new LinkedHashMap(5, 0.75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > MAX_SIZE;
        }
    };

    private final Map<String, LinkedList<String>> usernameKeyMap = new LinkedHashMap(5, 0.75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > MAX_SIZE;
        }
    };


    public synchronized BaseResponse<String> login(String username, String password, String ip) {
        try {
            String cookieValue = null;
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                return new BaseResponse<String>(false, "用户名或密码不能为空", null);
            }

            password = rsaCrypt.decryptFromBase64String(password);
            Date loginTime = new Date(Long.valueOf(password.substring(0, password.indexOf(","))));
            if (DateUtils.addSeconds(loginTime, 10).compareTo(Calendar.getInstance(TimeZone.getTimeZone("GMT +0000")).getTime()) > 0) {
                return new BaseResponse<String>(false, "登陆过期，请重试", null);
            }

            if (password.equals(usernamePasswordMap.get(username))) {
                cookieValue = UUID.randomUUID().toString().replace("-", "");
                LinkedList<String> cookieValueList = usernameKeyMap.get(username);
                if (cookieValueList == null) {
                    cookieValueList = new LinkedList<String>();
                    usernameKeyMap.put(username, cookieValueList);
                } else {
                    // 超过用户最大登录数，移除队列头部
                    if (cookieValueList.size() + 1 > MAX_ONLINE_PER_USER) {
                        for (int i = 0; i < cookieValueList.size() + 1 - MAX_ONLINE_PER_USER; i++) {
                            keyUserLoginInfoMap.remove(cookieValueList.getFirst());
                            cookieValueList.removeFirst();
                        }
                    }
                }
                keyUserLoginInfoMap.put(cookieValue, new UserLoginInfo(username, ip, new Date()));
                cookieValueList.add(cookieValue);

                return new BaseResponse<String>(true, "登录成功", null);
            }
            return new BaseResponse<String>(false, "用户名或密码错误", null);
        } catch (Exception e) {
            logger.error("登录异常", e);
            return new BaseResponse<String>(false, "登录服务异常，请稍后重试", null);
        }


    }


    public synchronized boolean logout(String cookieValue) {
        if (StringUtils.isBlank(cookieValue)) {
            return false;
        }
        UserLoginInfo userLoginInfo = keyUserLoginInfoMap.remove(cookieValue);
        if (null != userLoginInfo) {
            usernameKeyMap.get(userLoginInfo.getUsername()).remove(cookieValue);
        }
        return true;

    }

    public boolean validateLogin(String cookieValue, String ip, boolean setCurrUserLoginInfo) {
        if (StringUtils.isBlank(cookieValue) || StringUtils.isBlank(ip)) {
            return false;
        }
        UserLoginInfo userLoginInfo = keyUserLoginInfoMap.get(cookieValue);
        if (null == userLoginInfo) {
            return false;
        }
        if (ip.equals(userLoginInfo.getIp()) && usernameKeyMap.get(userLoginInfo.getUsername()).contains(cookieValue)) {
            if (setCurrUserLoginInfo) {
                CURRENT_LOGIN_USER.set(userLoginInfo);
            }
            return true;
        }
        return false;
    }


    public void removeCurrUserLoginInfo() {
        CURRENT_LOGIN_USER.remove();
    }


    public static String getCurrentUsername() {
        UserLoginInfo userLoginInfo = CURRENT_LOGIN_USER.get();
        if (null == userLoginInfo) {
            return null;
        }
        return userLoginInfo.getUsername();
    }


    public static UserLoginInfo getCurrentUserLoginInfo() {
        return CURRENT_LOGIN_USER.get();
    }


}

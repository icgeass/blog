package com.zeroq6.blog.operate.service.login;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService {

    private final static ThreadLocal<UserLoginInfo> CURRENT_LOGIN_USER = new ThreadLocal<UserLoginInfo>();

    private final static String PASS_SLAT = "_PASS_SLAT_";

    private final Integer MAX_SIZE = 10000;

    // 大于等于1
    private final Integer MAX_ONLINE_PER_USER = 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${login.cookie.name}")
    private String loginCookieName = "";


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


    public synchronized String login(String username, String password, String ip) {
        String cookieValue = null;
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }

        if (password.equals(usernamePasswordMap.get(username))) {
            cookieValue = UUID.randomUUID().toString().replace("-", "");
            LinkedList<String> cookieValueList = usernameKeyMap.get(username);
            if (cookieValueList == null) {
                cookieValueList = new LinkedList<String>();
                usernameKeyMap.put(username, cookieValueList);
            } else {
                if (cookieValueList.size() + 1 > MAX_ONLINE_PER_USER) {
                    for (int i = 0; i < cookieValueList.size() + 1 - MAX_ONLINE_PER_USER; i++) {
                        keyUserLoginInfoMap.remove(cookieValueList.getFirst());
                        cookieValueList.removeFirst();
                    }
                }
            }
            keyUserLoginInfoMap.put(cookieValue, new UserLoginInfo(username, ip, new Date()));
            cookieValueList.add(cookieValue);


            return cookieValue;
        }
        return null;

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

    public boolean validateAndSetCurrUserLoginInfo(String cookieValue, String ip) {
        if (StringUtils.isBlank(cookieValue) || StringUtils.isBlank(ip)) {
            return false;
        }
        UserLoginInfo userLoginInfo = keyUserLoginInfoMap.get(cookieValue);
        if (null == userLoginInfo) {
            return false;
        }
        if (ip.equals(userLoginInfo.getIp()) && usernameKeyMap.get(userLoginInfo.getUsername()).contains(cookieValue)) {
            CURRENT_LOGIN_USER.set(userLoginInfo);
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

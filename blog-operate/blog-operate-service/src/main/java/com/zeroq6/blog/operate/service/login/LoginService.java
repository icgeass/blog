package com.zeroq6.blog.operate.service.login;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zeroq6.blog.common.base.BaseResponseCode;
import com.zeroq6.common.security.RsaCrypt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LoginService {

    private final static ThreadLocal<UserLoginInfo> CURRENT_LOGIN_USER = new ThreadLocal<UserLoginInfo>();

    public final static String PASS_SLAT = "_PASS_SLAT_";

    private final Integer maxLoginUser = 10000;

    // 大于等于1
    private final Integer maxOnlinePerUser = 1;

    // 登陆超时秒数
    private final int loginExpireSeconds = 30;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${login.cookie.name}")
    private String loginCookieName = "";

    @Autowired
    private RsaCrypt rsaCrypt;


    private Map<String, String> usernamePasswordMap = new ConcurrentHashMap<String, String>();

    public void setUsernamePasswordMap(Map<String, String> usernamePasswordMap) {
        this.usernamePasswordMap = usernamePasswordMap;
    }

    // 用户cookieValue---->userLoginInfo
    // size=最大在线数，等于最大用户数*每个用户最大在线数
    private final Map<String, UserLoginInfo> keyUserLoginInfoMap = Collections.synchronizedMap(new LinkedHashMap(5, 0.75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > maxLoginUser * maxOnlinePerUser;
        }
    });

    // username---->cookieValueList
    // size=最大用户数
    private final Map<String, LinkedList<String>> usernameKeyMap = Collections.synchronizedMap(new LinkedHashMap(5, 0.75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > maxLoginUser;
        }
    });


    // 缓存登陆的一次验证
    private final LoadingCache<String, String> loginKeyCache =
            CacheBuilder.newBuilder()
                    .maximumSize(maxLoginUser * maxOnlinePerUser * 5 + 10) // 最大在线数的5倍 + 10
                    .expireAfterWrite(loginExpireSeconds * 2 + 10, TimeUnit.MINUTES) // 需要比登陆过期时间长 + 10
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return "";
                        }
                    });


    public BaseResponseCode<String> login(String username, String password, String ip) {
        try {
            String cookieValue = null;
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                return new BaseResponseCode<String>(BaseResponseCode.CODE_FAILED, "用户名或密码不能为空", null);
            }

            // 说明loginKey已经请求过，非法请求
            if(StringUtils.isNotBlank(loginKeyCache.get(password))){
                return new BaseResponseCode<String>(BaseResponseCode.CODE_FAILED, "非法请求，重复请求", null);
            }
            // 解密
            password = rsaCrypt.decryptFromBase64String(password);

            // 验证登陆时间
            // new Date().getTime()在java和js中都是国际标准时间戳（格林威治标准时间）
            if (new Date().getTime() - Long.valueOf(password.substring(0, password.indexOf(","))) > loginExpireSeconds) {
                return new BaseResponseCode<String>(BaseResponseCode.CODE_FAILED, "登陆过期，请重试", null);
            }

            // 记录该次loginKey
            loginKeyCache.put(password, password);

            String passwordSha1 = password.substring(password.indexOf(",") + 1);

            if (passwordSha1.equals(usernamePasswordMap.get(username))) {
                cookieValue = UUID.randomUUID().toString().replace("-", "");
                LinkedList<String> cookieValueList = usernameKeyMap.get(username);
                if (cookieValueList == null) {
                    cookieValueList = new LinkedList<String>();
                    usernameKeyMap.put(username, cookieValueList);
                } else {
                    // 超过用户最大登录数，移除队列头部
                    if (cookieValueList.size() + 1 > maxOnlinePerUser) {
                        for (int i = 0; i < cookieValueList.size() + 1 - maxOnlinePerUser; i++) {
                            keyUserLoginInfoMap.remove(cookieValueList.getFirst());
                            cookieValueList.removeFirst();
                        }
                    }
                }
                keyUserLoginInfoMap.put(cookieValue, new UserLoginInfo(username, ip, new Date()));
                cookieValueList.add(cookieValue);

                return new BaseResponseCode<String>(BaseResponseCode.CODE_SUCCESS, "登录成功", null);
            }
            return new BaseResponseCode<String>(BaseResponseCode.CODE_FAILED, "用户名或密码错误", null);
        } catch (Exception e) {
            logger.error("登录异常", e);
            return new BaseResponseCode<String>(BaseResponseCode.CODE_EXCEPTION, "登录服务异常，请稍后重试", null);
        }


    }


    public boolean logout(String cookieValue) {
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

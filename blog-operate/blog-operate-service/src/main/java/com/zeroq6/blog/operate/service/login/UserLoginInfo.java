package com.zeroq6.blog.operate.service.login;

import java.io.Serializable;
import java.util.Date;

public class UserLoginInfo implements Serializable {


    public final static long serialVersionUID = 1L;


    private String username;

    private String ip;

    private Date loginTime;


    public UserLoginInfo() {
    }

    public UserLoginInfo(String username, String ip, Date loginTime) {
        this.username = username;
        this.ip = ip;
        this.loginTime = loginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}

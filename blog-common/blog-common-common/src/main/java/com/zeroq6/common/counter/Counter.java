
package com.zeroq6.common.counter;


import java.util.Date;

public class Counter {

    private String type;
    private String key;
    //
    private int leftTimes;
    private boolean isLock;
    private Date unlockTime;
    private String message;
    //
    private Date lastUpdateTime;

    public Counter() {
        this.leftTimes = 5;
        this.isLock = false;
        this.unlockTime = null;
        this.message = null;
        //
        this.lastUpdateTime = new Date();
    }

    public Counter(String type, String key, int leftTimes, boolean isLock, Date unlockTime, String message) {
        this.type = type;
        this.key = key;
        //
        this.leftTimes = leftTimes;
        this.isLock = isLock;
        this.unlockTime = unlockTime;
        this.message = message;
        //
        this.lastUpdateTime = new Date();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLeftTimes() {
        return leftTimes;
    }

    public void setLeftTimes(int leftTimes) {
        this.leftTimes = leftTimes;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}

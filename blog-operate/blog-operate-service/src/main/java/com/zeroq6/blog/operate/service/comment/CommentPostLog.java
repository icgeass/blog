package com.zeroq6.blog.operate.service.comment;

import java.util.concurrent.atomic.AtomicInteger;

public class CommentPostLog {

    private long currentTimeMillis;

    private AtomicInteger cnt;

    public CommentPostLog(long currentTimeMillis, AtomicInteger cnt) {
        this.currentTimeMillis = currentTimeMillis;
        this.cnt = cnt;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public AtomicInteger getCnt() {
        return cnt;
    }

    public void setCnt(AtomicInteger cnt) {
        this.cnt = cnt;
    }
}

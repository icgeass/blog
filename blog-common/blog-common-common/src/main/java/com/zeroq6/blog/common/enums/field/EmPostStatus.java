package com.zeroq6.blog.common.enums.field;

import com.zeroq6.blog.common.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
public enum EmPostStatus implements EnumApi{

    /**
     *  文章 => post
     */
    WEI_FABU(1, "未发布"),
    YI_FABU(2, "已发布"),
;

    private final int value;
    private final String title;

    private EmPostStatus(int value, String title) {
        this.value = value;
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public String getTitle(){
        return title;
    }

    public int value() {
        return getValue();
    }

    public String title() {
        return getTitle();
    }

    public static EmPostStatus of(final int value) {
        for (EmPostStatus em : EmPostStatus.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmPostStatus.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmPostStatus em = of(value);
        return em.title();
    }

    public static String title(final int value){
        return getTitle(value);
    }

    @Override
    public String toString() {
        return this.title;
    }



}

package com.zeroq6.blog.common.enums;

import com.zeroq6.blog.common.domain.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public enum EmCacheKeyType implements EnumApi {

    CHANGE_IT(0, "请修改"),;

    private final int value;
    private final String title;

    private EmCacheKeyType(int value, String title) {
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

    public static EmCacheKeyType of(final int value) {
        for (EmCacheKeyType em : EmCacheKeyType.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmCacheKeyType.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmCacheKeyType em = of(value);
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
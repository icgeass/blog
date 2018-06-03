package com.zeroq6.blog.common.enums;

import com.zeroq6.blog.common.domain.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public enum EmPreventRepeatResult implements EnumApi {

    INSERT_SUCCESS(1, "插入成功"),
    INSERT_REPEAT(2, "插入重复")

    ;

    private final int value;
    private final String title;

    private EmPreventRepeatResult(int value, String title) {
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

    public static EmPreventRepeatResult of(final int value) {
        for (EmPreventRepeatResult em : EmPreventRepeatResult.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmPreventRepeatResult.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmPreventRepeatResult em = of(value);
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

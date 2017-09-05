package com.zeroq6.blog.common.enums;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public enum EmRedisKeyType implements EnumApi{

    CHANGE_IT(0, "请修改"),;

    private final int value;
    private final String title;

    private EmRedisKeyType(int value, String title) {
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

    public static EmRedisKeyType of(final int value) {
        for (EmRedisKeyType em : EmRedisKeyType.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmRedisKeyType.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmRedisKeyType em = of(value);
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
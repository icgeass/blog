package com.zeroq6.blog.common.enums;


/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public enum EmYn implements EnumApi{

    YES(1, "有效"),
    NO(0, "无效"),
    ;

    private final int value;
    private final String title;

    private EmYn(int value, String title) {
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

    public static EmYn of(final int value) {
        for (EmYn em : EmYn.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmYn.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmYn em = of(value);
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

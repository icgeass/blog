package com.zeroq6.blog.common.enums;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public enum EmOperate implements EnumApi{

    VIEW(1, "查看"),
    EDIT(2, "编辑"),
    ADD(3, "新增"),
    DELETE(4, "删除"),;

    private final int value;
    private final String title;

    private EmOperate(int value, String title) {
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

    public static EmOperate of(final int value) {
        for (EmOperate em : EmOperate.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmOperate.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmOperate em = of(value);
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
package com.zeroq6.blog.common.enums.field;

import com.zeroq6.blog.common.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
public enum EmRelationType implements EnumApi{

    /**
     *  关系 => relation
     */
    WEN_ZHANG_BIAOQIAN(1, "文章标签"),
    WEN_ZHANG_FENLEI(2, "文章分类"),
;

    private final int value;
    private final String title;

    private EmRelationType(int value, String title) {
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

    public static EmRelationType of(final int value) {
        for (EmRelationType em : EmRelationType.values()) {
            if (em.value == value) {
                return em;
            }
        }
        throw new RuntimeException("无法查找枚举值: " + EmRelationType.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmRelationType em = of(value);
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

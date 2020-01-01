package com.zeroq6.blog.common.domain.enums.field;

import com.zeroq6.blog.common.domain.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public enum EmPostSource implements EnumApi {

    /**
     *  文章 => post
     */
    YUANCHUANG(1, "原创"),
    QITA(2, "其他"),
    CSDN(3, "CSDN"),
    CNBLOGS(4, "CNBLOGS"),
    SOURCE_51CTO(5, "51CTO"),
    ITEYE(6, "ITEYE"),
    BAIDU_ZHIDAO(7, "百度知道")
;

    private final int value;
    private final String title;

    private EmPostSource(int value, String title) {
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

    public static EmPostSource of(final Integer value) {
        return of(value, false);
    }

    public static EmPostSource of(final Integer value, boolean acceptNull) {
        if(null != value){
            for (EmPostSource em : EmPostSource.values()) {
                if (em.value == value) {
                    return em;
                }
            }
        }
        if(acceptNull){
            return null;
        }
        throw new RuntimeException("无法查找枚举值: " + EmPostSource.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmPostSource em = of(value);
        return em.title();
    }

    public static String title(final int value){
        return getTitle(value);
    }

    @Override
    public String toString() {
        return this.title;
    }

    /**系统生成结束,请勿修改,重新生成会覆盖*/

    /**自定义开始 */

    /**自定义结束 */
}

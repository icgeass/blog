package com.zeroq6.blog.common.domain.enums.field;

import com.zeroq6.blog.common.domain.enums.EnumApi;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public enum EmDictDictType implements EnumApi {

    /**
     *  字典 => dict
     */
    FENLEI(1, "分类"),
    BIAOQIAN(2, "标签"),
    LIANJIE(3, "链接"),
    LISHI(4, "历史"),
    SHE_JIAO(5, "社交"),
    ZHAN_DIAN_XINXI(6, "站点信息"),
;

    private final int value;
    private final String title;

    private EmDictDictType(int value, String title) {
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

    public static EmDictDictType of(final Integer value) {
        return of(value, false);
    }

    public static EmDictDictType of(final Integer value, boolean acceptNull) {
        if(null != value){
            for (EmDictDictType em : EmDictDictType.values()) {
                if (em.value == value) {
                    return em;
                }
            }
        }
        if(acceptNull){
            return null;
        }
        throw new RuntimeException("无法查找枚举值: " + EmDictDictType.class.getSimpleName() + ", " + value);
    }

    public static String getTitle(final int value){
        EmDictDictType em = of(value);
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

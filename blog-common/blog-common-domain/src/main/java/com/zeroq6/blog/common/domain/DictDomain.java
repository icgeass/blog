package com.zeroq6.blog.common.domain;

import com.zeroq6.blog.common.base.BaseDomain;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public class DictDomain extends BaseDomain<DictDomain> {

    private static final long serialVersionUID = 1L;

    /**
    * 字典 => dict
    */
    public DictDomain(){
        // 默认无参构造
    }

    /**
     * "字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置
     */
    private Integer dictType;
    /**
     * 字典键
     */
    private String dictKey;
    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 获取"字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置 dictType
     *
     * @return "字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置
     */
    public Integer getDictType() {
        return dictType;
    }
    /**
     * 设置"字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置 dictType
     *
     * @param dictType "字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置
     */
    public DictDomain setDictType(Integer dictType) {
        this.dictType = dictType;
        return this;
    }

    /**
     * 获取字典键 dictKey
     *
     * @return 字典键
     */
    public String getDictKey() {
        return dictKey;
    }
    /**
     * 设置字典键 dictKey
     *
     * @param dictKey 字典键
     */
    public DictDomain setDictKey(String dictKey) {
        this.dictKey = dictKey;
        return this;
    }

    /**
     * 获取字典值 dictValue
     *
     * @return 字典值
     */
    public String getDictValue() {
        return dictValue;
    }
    /**
     * 设置字典值 dictValue
     *
     * @param dictValue 字典值
     */
    public DictDomain setDictValue(String dictValue) {
        this.dictValue = dictValue;
        return this;
    }


    /**系统生成结束,请勿修改,重新生成会覆盖*/

    /**自定义开始 */

    /**自定义结束 */
}

package com.zeroq6.blog.common.domain;

import com.zeroq6.blog.common.base.BaseDomain;

/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
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
     * 字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息
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
     * 描述
     */
    private String dictDesc;

    /**
     * 获取字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息 dictType
     *
     * @return
     */
    public Integer getDictType() {
        return dictType;
    }
    /**
     * 设置字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息 dictType
     *
     * @param dictType 字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息
     */
    public DictDomain setDictType(Integer dictType) {
        this.dictType = dictType;
        return this;
    }

    /**
     * 获取字典键 dictKey
     *
     * @return
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
     * @return
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

    /**
     * 获取描述 dictDesc
     *
     * @return
     */
    public String getDictDesc() {
        return dictDesc;
    }
    /**
     * 设置描述 dictDesc
     *
     * @param dictDesc 描述
     */
    public DictDomain setDictDesc(String dictDesc) {
        this.dictDesc = dictDesc;
        return this;
    }




}

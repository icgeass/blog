package com.zeroq6.blog.common.domain;

import com.zeroq6.blog.common.base.BaseDomain;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public class RelationDomain extends BaseDomain<RelationDomain> {

    private static final long serialVersionUID = 1L;

    /**
    * 关系 => relation
    */
    public RelationDomain(){
        // 默认无参构造
    }

    /**
     * 类型，1，文章标签，2，文章分类
     */
    private Integer type;
    /**
     * 父id
     */
    private String parentId;
    /**
     * 子id
     */
    private String childId;

    /**
     * 获取类型，1，文章标签，2，文章分类 type
     *
     * @return 类型，1，文章标签，2，文章分类
     */
    public Integer getType() {
        return type;
    }
    /**
     * 设置类型，1，文章标签，2，文章分类 type
     *
     * @param type 类型，1，文章标签，2，文章分类
     */
    public RelationDomain setType(Integer type) {
        this.type = type;
        return this;
    }

    /**
     * 获取父id parentId
     *
     * @return 父id
     */
    public String getParentId() {
        return parentId;
    }
    /**
     * 设置父id parentId
     *
     * @param parentId 父id
     */
    public RelationDomain setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * 获取子id childId
     *
     * @return 子id
     */
    public String getChildId() {
        return childId;
    }
    /**
     * 设置子id childId
     *
     * @param childId 子id
     */
    public RelationDomain setChildId(String childId) {
        this.childId = childId;
        return this;
    }


    /**系统生成结束,请勿修改,重新生成会覆盖*/

    /**自定义开始 */

    /**自定义结束 */
}

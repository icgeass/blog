package com.zeroq6.blog.common.base;

import com.zeroq6.common.base.BaseDomain;

import java.io.Serializable;
import java.util.List;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public interface BaseDao<T extends BaseDomain, KEY extends Serializable> {

    int insert(T t);

    int insertFillingId(T t);

    int insertBatch(List<T> list);

    int updateByKey(T t);

    /**
     * 设置extendMap中where参数，否则通过manager或service调用
     */
    int updateByCondition(T t);

    T selectByKey(KEY key);

    List<T> selectList(T t);

    int selectListCount(T t);

    int disableByKey(KEY key);

    int disableByCondition(T t);
}

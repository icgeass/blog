package com.zeroq6.blog.common.base;

import com.zeroq6.common.base.Page;
import com.zeroq6.common.base.BaseDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public abstract class BaseService<T extends BaseDomain, KEY extends Serializable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public abstract BaseManager<T, KEY> getManager();

    public int insert(T t){
        return getManager().insert(t);
    }

    public int insertFillingId(T t){
        return getManager().insertFillingId(t);
    }

    public int insertBatch(List<T> list){
        return getManager().insertBatch(list);
    }

    public int updateByKey(T t){
        return getManager().updateByKey(t);
    }

    public int updateByCondition(T t, T where){
        return getManager().updateByCondition(t, where);
    }

    public int updateByCondition(T t, T where, Integer expectedRows){
        return getManager().updateByCondition(t, where, expectedRows);
    }

    public T selectByKey(KEY key){
        return getManager().selectByKey(key);
    }

    public List<T> selectList(T t){
        return getManager().selectList(t);
    }

    public int selectListCount(T t){
        return getManager().selectListCount(t);
    }

    public Page<T> selectPage(T t, Page<T> page){
        return getManager().selectPage(t, page);
    }

    public T selectOne(T t) {
        return getManager().selectOne(t);
    }

    public T selectOne(T t, boolean acceptNull) {
        return getManager().selectOne(t, acceptNull);
    }

    public int disableByKey(KEY key){
        return getManager().disableByKey(key);
    }

    public int disableByCondition(T t){
        return getManager().disableByCondition(t);
    }

    public int disableByCondition(T t, Integer expectedRows){
        return getManager().disableByCondition(t, expectedRows);
    }

}

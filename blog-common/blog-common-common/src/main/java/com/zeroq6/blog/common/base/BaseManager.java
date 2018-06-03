package com.zeroq6.blog.common.base;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public abstract class BaseManager<T extends BaseDomain, KEY extends Serializable> {

    private final static List<String> ignoredFieldList = new ArrayList<String>() {{
        add("class");
        add("extendMap");
        add("yn");
        add("orderField");
        add("orderFieldType");
        add("startIndex");
        add("endIndex");
        add("pageSize");
    }};

    private final static Map<String, List<Method>> classMethodMap = new HashMap<String, List<Method>>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public abstract BaseDao<T, KEY> getDao();


    /**
     * insert
     *
     * @param t
     * @return
     */
    public int insert(T t) {
        int re = getDao().insert(t);
        if (re != 1) {
            throw new RuntimeException(t.getClass().getSimpleName() + ": insert影响行数不为1");
        }
        return re;
    }

    public int insertFillingId(T t) {
        int re = getDao().insertFillingId(t);
        if (re != 1) {
            throw new RuntimeException(t.getClass().getSimpleName() + ": insertFillingId影响行数不为1");
        }
        if(null == t.getId()){
            throw new RuntimeException(t.getClass().getSimpleName() + ": insertFillingId Mybatis未填充id字段");
        }
        return re;
    }

    public int insertBatch(List<T> list) {
        if (null == list || list.isEmpty()) {
            throw new RuntimeException("insertBatch集合list不能为空");
        }
        int re = getDao().insertBatch(list);
        if (re != list.size()) {
            throw new RuntimeException(String.format("%s, insertBatch实际影响行数: %s与list大小: %s不相同", list.get(0).getClass().getSimpleName(), String.valueOf(re), String.valueOf(list.size())));
        }
        return re;
    }

    /**
     * update
     *
     * @param t
     * @return
     */
    public int updateByKey(T t) {
        int re = getDao().updateByKey(t);
        if (re != 1) {
            throw new RuntimeException("updateByKey影响行数不为1, id: " + t.getId());
        }
        return re;
    }

    public int updateByCondition(T t, T where) {
        return updateByCondition(t, where, null);
    }

    public int updateByCondition(T t, T where, Integer expectedRows) {
        if (null == t || null == where) {
            throw new RuntimeException("更新后内容和where条件不能为空");
        }
        int re = 0;
        try {
            // 校验更新条件，where必须带条件
            validateConditionNum(where, 1);
            t.getExtendMap().put("where", where);
            // 执行
            re = getDao().updateByCondition(t);
            // 判断影响行数是否符合预期
            if (null != expectedRows) {
                if (re != expectedRows) {
                    throw new RuntimeException(String.format("%s, 实际影响行数: %s与预期影响行数: %s不一致, update: %s", t.getClass().getSimpleName(), String.valueOf(re), String.valueOf(expectedRows), JSON.toJSONString(t)));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return re;
    }


    /**
     * select
     *
     * @param key
     * @return
     */
    public T selectByKey(KEY key) {
        return getDao().selectByKey(key);
    }

    public List<T> selectList(T t) {
        return getDao().selectList(t);
    }

    public int selectListCount(T t) {
        return getDao().selectListCount(t);
    }


    public T selectLimitOne(T t, boolean acceptNull) {
        t.setStartIndex(0).setEndIndex(1);
        List<T> list = selectList(t);
        if (null == list || list.isEmpty()) {
            if (!acceptNull) {
                throw new RuntimeException(t.getClass().getSimpleName() + ": selectLimitOne没有查询到数据, where: " + JSON.toJSONString(t));
            }
            return null;
        } else {
            return list.get(0);
        }
    }

    public T selectLimitOne(T t){
        return selectLimitOne(t, false);
    }

    public T selectOne(T t, boolean acceptNull) {
        t.setStartIndex(0).setEndIndex(2); // 取2条，提高性能
        List<T> list = selectList(t);
        if (null == list || list.isEmpty()) {
            if(!acceptNull){
                throw new RuntimeException(t.getClass().getSimpleName() + ": selectOne没有查询到数据, where: " + JSON.toJSONString(t));
            }
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new RuntimeException(String.format("%s: selectOne查询到多条数据, where: %s", t.getClass().getSimpleName(), JSON.toJSONString(t)));
        }
    }

    public T selectOne(T t){
        return selectOne(t, false);
    }

    /**
     * t 表示查询条件，page表示分页
     *
     * @param t
     * @param page
     * @return
     */
    public Page<T> selectPage(T t, Page<T> page) {
        Integer size = this.selectListCount(t);
        if (size == null || size <= 0) {
            page.setTotalCount(0);
            page.setData(new ArrayList<T>());
        }else {
            page.setTotalCount(size);
            if(page.getCurrentPage() > page.getPageCount()){
                page.setCurrentPage(page.getPageCount());
            }
            // T继承自BaseQuery，BaseQuery中使用endIndex-startIndex获得页大小，mybatis中用到
            t.setStartIndex(page.getStartIndex()).setEndIndex(page.getEndIndex());
            page.setData(this.selectList(t));
        }
        return page;
    }

    /**
     * delete
     *
     * @param key
     * @return
     */

    public int disableByKey(KEY key) {
        int re = getDao().disableByKey(key);
        if (re != 1) {
            throw new RuntimeException("disableByKey影响行数不为1, id: " + key);
        }
        return re;
    }

    public int disableByCondition(T t) {
        return disableByCondition(t, null);
    }

    public int disableByCondition(T t, Integer expectedRows) {
        validateConditionNum(t, 1);
        int re = getDao().disableByCondition(t);
        if (null != expectedRows) {
            if (re != expectedRows) {
                throw new RuntimeException(String.format("%s, 实际影响行数: %s与预期影响行数: %s不一致, where: ", t.getClass().getSimpleName(), String.valueOf(re), String.valueOf(expectedRows), JSON.toJSONString(t)));
            }
        }
        return re;
    }



    /**
     * 按条件更新验证条件个数是否满足，不满足则异常回滚（事务单独配置）
     * @param where
     * @param minNum
     */
    private void validateConditionNum(T where, int minNum){
        try{
            // 校验更新条件，where必须带条件
            int fieldNotNullNumber = 0;
            String className = where.getClass().getCanonicalName();
            if (null == classMethodMap.get(className)) {
                synchronized (BaseManager.class){
                    classMethodMap.put(className, new ArrayList<Method>());
                    PropertyDescriptor[] des = Introspector.getBeanInfo(where.getClass()).getPropertyDescriptors();
                    for (PropertyDescriptor d : des) {
                        String fieldName = d.getName();
                        if (!ignoredFieldList.contains(fieldName)) {
                            classMethodMap.get(className).add(d.getReadMethod());
                        }
                    }
                }
            }
            for (Method method : classMethodMap.get(className)) {
                if (null != method.invoke(where)) {
                    fieldNotNullNumber++;
                }
            }
            if (fieldNotNullNumber < minNum) {
                throw new RuntimeException("updateByCondition中where至少应含" + minNum + "个条件, 不包含在内的字段" + ignoredFieldList);
            }
        }catch (Exception e){
            throw new RuntimeException("更新验证where条件个数异常, " + e.getMessage(), e);
        }
    }


}

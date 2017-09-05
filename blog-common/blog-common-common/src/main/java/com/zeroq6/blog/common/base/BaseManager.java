package com.zeroq6.blog.common.base;

import com.zeroq6.blog.common.enums.EmYn;
import com.zeroq6.common.base.Page;
import com.zeroq6.common.base.BaseDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
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
        preInsertOrUpdate(t, true);
        int re = getDao().insert(t);
        if (re != 1) {
            throw new RuntimeException(t.getClass().getSimpleName() + ": insert影响行数不为1");
        }
        return re;
    }

    public int insertFillingId(T t) {
        preInsertOrUpdate(t, true);
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
            return 0;
        }
        for (T t : list) {
            preInsertOrUpdate(t, true);
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
        preInsertOrUpdate(t, false);
        int re = getDao().updateByKey(t);
        if (re != 1) {
            throw new RuntimeException("updateByKey影响行数不为1");
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
            // 预处理更新条件
            where.setYn(EmYn.YES.value());
            t.getExtendMap().put("where", where);
            // 预处理更新内容
            preInsertOrUpdate(t, false);
            // 执行
            re = getDao().updateByCondition(t);
            // 判断影响行数是否符合预期
            if (null != expectedRows) {
                if (re != expectedRows) {
                    throw new RuntimeException(String.format("%s, 实际影响行数: %s与预期影响行数: %s不一致", t.getClass().getSimpleName(), String.valueOf(re), String.valueOf(expectedRows)));
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
        preSelect(t);
        return getDao().selectList(t);
    }

    public int selectListCount(T t) {
        preSelect(t);
        return getDao().selectListCount(t);
    }

    public T selectOne(T t, boolean acceptNull) {
        List<T> list = selectList(t);
        if (null == list || list.isEmpty()) {
            if(!acceptNull){
                throw new RuntimeException(t.getClass().getSimpleName() + ": selectOne没有查询到数据");
            }
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new RuntimeException(String.format("%s: selectOne查询到%s条数据", t.getClass().getSimpleName(), String.valueOf(list.size())));
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
            throw new RuntimeException("disableByKey影响行数不为1");
        }
        return re;
    }

    public int disableByCondition(T t) {
        return disableByCondition(t, null);
    }

    public int disableByCondition(T t, Integer expectedRows) {
        validateConditionNum(t, 1);
        preDisable(t);
        int re = getDao().disableByCondition(t);
        if (null != expectedRows) {
            if (re != expectedRows) {
                throw new RuntimeException(String.format("%s, 实际影响行数: %s与预期影响行数: %s不一致", t.getClass().getSimpleName(), String.valueOf(re), String.valueOf(expectedRows)));
            }
        }
        return re;
    }

    /**
     * 预处理写入和更新（按KEY）操作，传入的t为将要更新的内容
     *
     * @param t
     * @param insert
     */
    private void preInsertOrUpdate(T t, boolean insert) {
        Date now = new Date();
        if (insert) {
            t.setId(null);
            t.setCreatedTime(now);
        }
        t.setYn(EmYn.YES.value()).setModifiedTime(now);

    }

    /**
     * 预处理disable，传入的t为where条件
     *
     * @param t
     */
    private void preDisable(T t) {
        if (null != t) {
            t.setYn(EmYn.YES.value());
        }
    }

    /**
     * 预处理select，传入的t为where条件
     *
     * @param t
     */
    private void preSelect(T t) {
        if (null != t) {
            t.setYn(EmYn.YES.value()); // sql里面写死了t.yn=1
        }
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
                Object obj = method.invoke(where);
                if (null != method.invoke(where)) {
                    if(obj instanceof String){
                        if(((String)obj).trim().length() > 0){
                            fieldNotNullNumber++;
                        }
                    }else{
                        fieldNotNullNumber++;
                    }
                }
            }
            if (fieldNotNullNumber < minNum) {
                throw new RuntimeException("updateByCondition中where至少含" + minNum + "个条件，不包含在内的字段" + ignoredFieldList);
            }
        }catch (Exception e){
            throw new RuntimeException("更新验证where条件个数异常, " + e.getMessage(), e);
        }
    }


}

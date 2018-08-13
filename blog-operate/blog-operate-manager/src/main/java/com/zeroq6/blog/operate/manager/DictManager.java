package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.dao.DictDao;
import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
@Service
public class DictManager extends BaseManager<DictDomain, Long> implements InitializingBean {

    @Autowired
    private DictDao dictDao;


    private List<DictDomain> dictDomainCacheList;


    @Override
    public BaseDao<DictDomain, Long> getDao() {
        return dictDao;
    }



    public void flushDictList() {
        try {
            dictDomainCacheList = selectList(null);
            logger.info("刷新字典成功");
        } catch (Exception e) {
            logger.error("刷新字典失败", e);
        }
    }

    public List<DictDomain> getDictByType(EmDictDictType type) {
        List<DictDomain> result = new ArrayList<DictDomain>();
        for (DictDomain dictDomain : dictDomainCacheList) {
            if (dictDomain.getDictType() == type.value()) {
                result.add(dictDomain);
            }
        }
        return result;
    }


    public DictDomain getDictByTypeAndKey(EmDictDictType type, String key) {
        return getDictByTypeAndKey(type, key, false);
    }

    public DictDomain getDictByTypeAndKey(EmDictDictType type, String key, boolean acceptNull) {
        List<DictDomain> result = new ArrayList<DictDomain>();
        for (DictDomain dictDomain : dictDomainCacheList) {
            if (dictDomain.getDictType() == type.value() && dictDomain.getDictKey().equals(key)) {
                result.add(dictDomain);
            }
        }
        int size = result.size();
        if (size == 0) {
            if(acceptNull){
                return null;
            }
            throw new RuntimeException("字典表, type: " + type + ", key: " + key + "在字典中记录条数为0");
        } else if (size == 1) {
            return result.get(0);
        } else {
            throw new RuntimeException("字典表, type: " + type + ", key: " + key + "在字典中记录条数大于1");
        }
    }


    public Map<String, String> transferMap(List<DictDomain> dictDomainList) {
        Map<String, String> result = new HashMap<String, String>();
        if (null == dictDomainList || dictDomainList.isEmpty()) {
            return result;
        }
        for (DictDomain item : dictDomainList) {
            result.put(item.getDictKey(), item.getDictValue());
        }
        return result;
    }

    @Transactional
    public void saveOrUpdate(DictDomain dictDomain){
        if(null == dictDomain.getId() || dictDomain.getId() <= 0L){
            insert(dictDomain);
        }else{
            updateByKey(dictDomain);
        }
        flushDictList();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        flushDictList();
    }


}

package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.dao.DictDao;
import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.domain.DictDomain;
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

    public List<DictDomain> getDictByType(int type) {
        return getDictByTypeAndKeyList(type, null);
    }

    public List<DictDomain> getDictByTypeList(List<Integer> typeList) {
        List<DictDomain> list = new ArrayList<DictDomain>();
        for (Integer type : typeList) {
            list.addAll(getDictByType(type));
        }
        return list;
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

    public List<DictDomain> getDictByTypeAndKeyList(int type, List<String> dictKeyList) {
        List<DictDomain> result = new ArrayList<DictDomain>();
        for (DictDomain dictDomain : dictDomainCacheList) {
            if (dictDomain.getDictType() == type) {
                if (null == dictKeyList || dictKeyList.isEmpty()) {
                    result.add(dictDomain);
                } else {
                    if (dictKeyList.contains(dictDomain.getDictKey())) {
                        result.add(dictDomain);
                    }
                }
            }
        }
        return result;
    }

    public DictDomain getDictByTypeAndKey(int type, String key) {
        List<DictDomain> result = new ArrayList<DictDomain>();
        for (DictDomain dictDomain : dictDomainCacheList) {
            if (dictDomain.getDictType() == type && dictDomain.getDictKey().equals(key)) {
                result.add(dictDomain);
            }
        }
        if (result.size() != 1) {
            throw new RuntimeException("字典表, type: " + type + ", key: " + key + "在字典中记录条数不为1");
        }
        return result.get(0);
    }

    public List<String> getValueListByType(int type) {
        List<String> valueList = new ArrayList<String>();
        List<DictDomain> dictDomainList = getDictByType(type);
        for (DictDomain dictDomain : dictDomainList) {
            valueList.add(dictDomain.getDictValue());
        }
        return valueList;
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

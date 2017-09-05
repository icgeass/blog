package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.dao.RelationDao;
import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.domain.RelationDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
@Service
public class RelationManager extends BaseManager<RelationDomain, Long> {

    @Autowired
    private RelationDao relationDao;



    @Override
    public BaseDao<RelationDomain, Long> getDao() {
        return relationDao;
    }




}

package com.zeroq6.blog.operate.service;

import com.zeroq6.blog.common.domain.RelationDomain;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.operate.manager.RelationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class RelationService extends BaseService<RelationDomain, Long> {

    @Autowired
    private RelationManager relationManager;



    @Override
    public BaseManager<RelationDomain, Long> getManager() {
        return relationManager;
    }





}

package com.zeroq6.blog.operate.service;

import com.zeroq6.blog.common.domain.AttachDomain;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.operate.manager.AttachManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class AttachService extends BaseService<AttachDomain, Long> {

    @Autowired
    private AttachManager attachManager;



    @Override
    public BaseManager<AttachDomain, Long> getManager() {
        return attachManager;
    }





}

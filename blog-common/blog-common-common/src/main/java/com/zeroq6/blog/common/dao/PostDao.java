package com.zeroq6.blog.common.dao;

import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.domain.PostDomain;
import org.springframework.stereotype.Service;


/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
@Service
public interface PostDao extends BaseDao<PostDomain, Long> {



    PostDomain selectPrevPost(Long id);

    PostDomain selectNextPost(Long id);


}

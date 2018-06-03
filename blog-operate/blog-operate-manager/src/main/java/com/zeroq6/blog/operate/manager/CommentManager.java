package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.dao.CommentDao;
import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
@Service
public class CommentManager extends BaseManager<CommentDomain, Long> {

    @Autowired
    private CommentDao commentDao;


    @Autowired
    private PostManager postManager;


    @Override
    public BaseDao<CommentDomain, Long> getDao() {
        return commentDao;
    }




    @Transactional(value = "tx", rollbackFor = Exception.class)
    public BaseResponse<String> post(CommentDomain commentDomain, PostDomain postDomain) {
        insertFillingId(commentDomain);
        return new BaseResponse<String>(true, "成功", commentDomain.getId() + "");
    }

}

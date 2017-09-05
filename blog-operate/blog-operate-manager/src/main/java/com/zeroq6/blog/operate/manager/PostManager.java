package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.dao.PostDao;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.RelationDomain;
import com.zeroq6.blog.common.enums.field.EmRelationType;
import com.zeroq6.common.base.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 自定义开始 自定义结束
 * 自定义结束
 */


/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
@Service
public class PostManager extends BaseManager<PostDomain, Long> {

    @Autowired
    private PostDao postDao;

    @Autowired
    private DictManager dictManager;

    @Autowired
    private CommentManager commentManager;

    @Autowired
    private RelationManager relationManager;



    @Override
    public BaseDao<PostDomain, Long> getDao() {
        return postDao;
    }





    @Transactional
    public BaseResponse<String> deleteById(Long id) {
        if (null == id || id <= 0) {
            throw new RuntimeException("文章id非法, " + id);
        }
        this.disableByKey(id);
        relationManager.disableByCondition(new RelationDomain().setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value()).setParentId(id + ""));
        relationManager.disableByCondition(new RelationDomain().setType(EmRelationType.WEN_ZHANG_FENLEI.value()).setParentId(id + ""));
        commentManager.disableByCondition(new CommentDomain().setPostId(id));
        return new BaseResponse<String>(true, "成功", "成功");
    }


    @Transactional
    public BaseResponse<String> addPost(PostDomain postDomain, RelationDomain category, List<RelationDomain> tagsList) {
        // 文章
        insertFillingId(postDomain);
        if (null != category && null != tagsList && !tagsList.isEmpty()) {
            // 分类
            category.setParentId(postDomain.getId() + "");
            relationManager.insert(category);
            // 标签
            for (RelationDomain tag : tagsList) {
                tag.setParentId(postDomain.getId() + "");
            }
            relationManager.insertBatch(tagsList);
        }
        return new BaseResponse<String>(true, "成功", "成功");
    }

    @Transactional
    public BaseResponse<String> editPost(PostDomain postDomain, String categoryId, List<RelationDomain> addList, List<RelationDomain> deleteList) {
        updateByKey(postDomain);
        if (StringUtils.isNotBlank(categoryId)) {
            relationManager.updateByCondition(new RelationDomain().setChildId(categoryId), new RelationDomain().setType(EmRelationType.WEN_ZHANG_FENLEI.value()).setParentId(postDomain.getId() + ""), 1);

            relationManager.insertBatch(addList);
            for (RelationDomain delete : deleteList) {
                relationManager.disableByKey(delete.getId());
            }
        }
        return new BaseResponse<String>(true, "成功", "成功");
    }

}

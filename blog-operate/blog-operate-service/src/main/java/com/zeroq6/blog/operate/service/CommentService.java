package com.zeroq6.blog.operate.service;

import com.alibaba.fastjson.JSON;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.enums.field.EmCommentParentType;
import com.zeroq6.blog.common.domain.enums.field.EmPostPostType;
import com.zeroq6.blog.common.domain.enums.field.EmPostStatus;
import com.zeroq6.blog.operate.manager.CommentManager;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 自定义开始 自定义结束
 */


/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class CommentService extends BaseService<CommentDomain, Long> {

    @Autowired
    private CommentManager commentManager;


    @Autowired
    private PostService postService;


    @Override
    public BaseManager<CommentDomain, Long> getManager() {
        return commentManager;
    }





    /**
     * 提交评论
     * @param commentDomain
     * @return
     */
    public BaseResponse<String> post(CommentDomain commentDomain) {
        try {
            // 校验
            if (StringUtils.isBlank(commentDomain.getUsername())) {
                throw new RuntimeException("称呼不能为空");
            }
            if (!EmailValidator.getInstance().isValid(commentDomain.getEmail())) {
                throw new RuntimeException("邮箱格式错误");
            }
            if (null != commentDomain.getUrl() && !UrlValidator.getInstance().isValid(commentDomain.getUrl())) {
                throw new RuntimeException("url格式错误");
            }
            if(StringUtils.isBlank(commentDomain.getContent())){
                throw new RuntimeException("评论内容不能为空");
            }
            if (null == commentDomain.getPostId() || null == commentDomain.getParentType() || null == commentDomain.getParentId()) {
                throw new RuntimeException("文章id，关联id，关联类型不能为空");
            }
            // 文章id是否非法
            PostDomain query = new PostDomain();
            query.setId(commentDomain.getPostId());
            query.setStatus(EmPostStatus.YI_FABU.value());
            query.put("postTypeIn", Arrays.asList(new Integer[]{EmPostPostType.WENZHANG.value(), EmPostPostType.LIUYAN.value()}));
            PostDomain post = postService.selectOne(query);
            //
            if (commentDomain.getParentType() == EmCommentParentType.PINGLUN.value()) {
                this.selectOne(new CommentDomain().setPostId(commentDomain.getPostId()).setId(commentDomain.getParentId()), false);
            }else if(commentDomain.getParentType() == EmCommentParentType.WENZHANG.value()){
                commentDomain.setParentId(commentDomain.getPostId());
            }else{
                throw new RuntimeException("评论父类型非法, " + commentDomain.getParentType());
            }
            return commentManager.post(commentDomain, post);
        } catch (Exception e) {
            logger.error("提交评论异常, " + JsonUtils.toJSONString(commentDomain), e);
            return new BaseResponse<String>(false, e.getMessage(), null);
        }
    }



}

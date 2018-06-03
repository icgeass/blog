package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.enums.field.EmPostPostType;
import com.zeroq6.blog.operate.service.CommentService;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.common.utils.MyWebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuuki asuna on 2017/5/22.
 */
@Controller
@RequestMapping("/comment")
public class CommentController extends BaseController {


    private final String postUri = "/post/show/%s";


    private final String guestBookUri = "/guestbook";


    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/post")
    public String post(CommentDomain commentDomain, HttpServletRequest request, Model view) {
        String userAgent = request.getHeader("user-agent");
        if (StringUtils.isBlank(userAgent)) {
            return redirect(commentDomain);
        }
        String referer = request.getHeader("referer");
        if (StringUtils.isBlank(referer)) {
            return redirect(commentDomain);
        }
        // ip UA
        commentDomain.setIp(MyWebUtils.getClientIp(request));
        commentDomain.setUserAgent(userAgent);
        // trim
        commentDomain.setUsername(StringUtils.trim(commentDomain.getUsername()));
        commentDomain.setEmail(StringUtils.trim(commentDomain.getEmail()));
        commentDomain.setUrl(StringUtils.trim(commentDomain.getUrl()));
        commentDomain.setContent(StringUtils.trim(commentDomain.getContent()));
        //
        BaseResponse<String> result = commentService.post(commentDomain);
        if (result.isSuccess()) {
            return redirect(commentDomain) + "#c" + commentDomain.getId();
        }
        return null;
    }

    private String redirect(CommentDomain commentDomain) {
        if (null == commentDomain || null == commentDomain.getPostId()) {
            return redirectIndex();
        }
        PostDomain postDomain = postService.selectOne(new PostDomain().setId(commentDomain.getPostId()), true);
        if (null == postDomain) {
            return redirectIndex();
        }
        if (postDomain.getPostType() == EmPostPostType.WENZHANG.value()) {
            return "redirect:" + String.format(postUri, postDomain.getId() + "");
        } else if (postDomain.getPostType() == EmPostPostType.LIUYAN.value()) {
            return "redirect:" + guestBookUri;
        } else {
            return redirectIndex();
        }
    }

}

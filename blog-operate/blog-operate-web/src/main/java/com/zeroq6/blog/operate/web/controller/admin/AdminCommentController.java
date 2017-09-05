package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.operate.service.CommentService;
import com.zeroq6.common.base.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yuuki asuna on 2017/8/10.
 */

@Controller
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping("")
    public String list(CommentDomain commentDomain, Page<CommentDomain> page, Model view) {
        commentService.selectPage(commentDomain, page);
        view.addAttribute("page", page);
        view.addAttribute("commentDomain", commentDomain);
        return "/admin/comment/commentList";
    }
}

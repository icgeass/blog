package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.operate.service.CommentService;
import com.zeroq6.blog.common.base.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model view) {
        view.addAttribute("comment", commentService.selectOne(new CommentDomain().setId(id), true));
        return "/admin/comment/commentEdit";
    }

    @RequestMapping("/save")
    public String save(CommentDomain commentDomain, Model view) {
        commentService.updateByKey(commentDomain);
        return "redirect:/admin/comment";
    }


    @RequestMapping("/delete/{id}")
    public String del(@PathVariable Long id, Model view) {
        commentService.deleteCommentById(id);
        return "forward:/admin/comment";
    }
}

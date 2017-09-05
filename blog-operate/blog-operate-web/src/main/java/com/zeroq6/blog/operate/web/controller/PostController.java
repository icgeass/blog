package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.enums.field.EmPostPostType;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.common.base.BaseResponse;
import com.zeroq6.common.base.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by yuuki asuna on 2017/5/22.
 */
@Controller
@RequestMapping("/post")
public class PostController extends BaseController {


    @Autowired
    private PostService postService;

    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute("menu", "index");
        model.addAttribute("categoryTitle", "首页");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(String page, Model view) throws Exception {
        BaseResponse<Page<PostDomain>> result = postService.index(page);
        if (result.isSuccess()) {
            view.addAttribute("page", result.getBody());
            return "/index";
        }
        return null;

    }

    @RequestMapping(value = "/show/{id}")
    public String show(@PathVariable Long id, Model view) throws Exception {
        BaseResponse<Map<String, Object>> result = postService.show(id);
        if (result.isSuccess()) {
            view.addAllAttributes(result.getBody());
            return "/post";
        }
        return null;
    }


}

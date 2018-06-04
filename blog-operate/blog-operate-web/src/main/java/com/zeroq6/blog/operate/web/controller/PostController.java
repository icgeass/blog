package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.blog.common.base.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
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
        model.addAttribute(NAME_MENU, "index");
        model.addAttribute(NAME_CATEGORY_TITLE, "首页");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(String page, Model view) throws Exception {
        try {
            BaseResponse<Page<PostDomain>> result = postService.index(page);
            if (result.isSuccess()) {
                view.addAttribute("page", result.getBody());
                return "/index";
            }
        } catch (Exception e) {
            logger.error("首页文章列表异常", e);
        }
        return redirectIndex();
    }

    @RequestMapping(value = "/show/{id}")
    public String show(@PathVariable Long id, Model view) throws Exception {

        try {
            BaseResponse<Map<String, Object>> result = postService.show(id);
            if (result.isSuccess()) {
                view.addAllAttributes(result.getBody());
                return "/post";
            }
        } catch (Exception e) {
            logger.error("文章详情异常: " + id, e);
        }
        return redirectIndex();
    }


}

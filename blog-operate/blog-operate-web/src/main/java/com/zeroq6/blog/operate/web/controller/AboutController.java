package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.operate.service.DictService;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by yuuki asuna on 2017/5/24.
 */
@Controller
@RequestMapping("/about")
public class AboutController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private DictService dictService;


    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute(NAME_MENU, "about");
        model.addAttribute(NAME_CATEGORY_TITLE, "关于");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(Model view) {
        try {
            BaseResponse<Map<String, String>> result = dictService.getAboutInfo();
            if (result.isSuccess()) {
                view.addAllAttributes(result.getBody());
                return "/about";
            }
        } catch (Exception e) {
            logger.error("关于页面异常", e);
        }
        return redirectIndex();

    }
}

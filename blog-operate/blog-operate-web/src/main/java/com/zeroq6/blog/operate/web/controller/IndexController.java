package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.operate.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Controller("/")
public class IndexController extends BaseController{

    @Autowired
    private PostService postService;

    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String index() throws Exception {
        return "redirect:/post" ;
    }

    @RequestMapping(value = "/404", method = {RequestMethod.GET})
    public String notFound(Model model) throws Exception {
        model.addAttribute(NAME_MENU, "404");
        model.addAttribute(NAME_CATEGORY_TITLE, "404");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
        return "/404" ;
    }


}



package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
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
@RequestMapping("/guestbook")
public class GuestBookController extends BaseController {

    @Autowired
    private PostService postService;

    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute("menu", "guestbook");
        model.addAttribute("categoryTitle", "留言");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(Model view) {
        BaseResponse<Map<String, Object>> result = postService.getGuestBook();
        if (result.isSuccess()) {
            view.addAllAttributes(result.getBody());
            return "/guestbook";
        }
        return null;
    }

}

package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.blog.operate.service.captcha.CaptchaService;
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

    @Autowired
    private CaptchaService captchaService;

    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute(NAME_MENU, "guestbook");
        model.addAttribute(NAME_CATEGORY_TITLE, "留言");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(Model view) {
        try {
            BaseResponse<Map<String, Object>> result = postService.getGuestBook();
            if (result.isSuccess()) {
                view.addAllAttributes(result.getBody());
                view.addAllAttributes(captchaService.getNewCaptcha());
                return "/guestbook";
            }
        } catch (Exception e) {
            logger.error("留言板页面异常", e);
        }
        return redirectIndex();
    }

}

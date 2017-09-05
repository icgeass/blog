package com.zeroq6.blog.operate.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by yuuki ausna on 2017/8/11.
 */

@Controller
@RequestMapping("/admin")
public class AdminIndexController {


    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String index() throws Exception {
        return "redirect:/admin/post" ;
    }
}

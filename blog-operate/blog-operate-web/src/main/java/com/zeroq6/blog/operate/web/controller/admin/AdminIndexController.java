package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.sso.web.client.interceptor.LoginInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "logout", method = {RequestMethod.GET})
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoginInterceptor.logout(request, response);
        return null ;
    }


}

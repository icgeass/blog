package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Controller("/")
public class IndexController extends BaseController{

    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String index() throws Exception {
        return "redirect:/post" ;
    }

    @RequestMapping(value = "/404", method = {RequestMethod.GET})
    public String notFound() throws Exception {
        return "/404" ;
    }


}



package com.zeroq6.blog.operate.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zeroq6.blog.operate.service.captcha.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping("/new")
    @ResponseBody
    public JSONObject captcha() {
        try{
            return captchaService.getNewCaptcha();
        }catch (Exception e){
            logger.error("/captcha/new error", e);
        }
        return new JSONObject();

    }


    @RequestMapping("/validate")
    @ResponseBody
    public JSONObject validate(@RequestParam(name = "captchaKey") String captchaKey, @RequestParam(name = "captchaValue") String captchaValue){
        JSONObject jsonObject = new JSONObject();
        try{
            boolean success = captchaService.validate(captchaKey, captchaValue);
            jsonObject.put("success", success);
            return jsonObject;
        }catch (Exception e){
            logger.error("/captcha/validate error", e);
        }
        return jsonObject;
    }

}

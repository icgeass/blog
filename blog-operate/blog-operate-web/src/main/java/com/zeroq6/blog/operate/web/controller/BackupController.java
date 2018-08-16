package com.zeroq6.blog.operate.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.service.BackupService;
import com.zeroq6.common.mail.MailReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author
 * @date 2018/8/16
 */
@Controller()
@RequestMapping("/backup")
public class BackupController extends BaseController{


    @Autowired
    private BackupService backupService;


    @RequestMapping(value = "/now")
    public String receive(HttpServletResponse response) throws Exception {
        try{
            backupService.backup();
            outJson(response, "success");
        }catch (Exception e){
            logger.error("接收邮件异常", e);
            outJson(response, "error: " + e.getMessage());
        }
        return null;
    }
}





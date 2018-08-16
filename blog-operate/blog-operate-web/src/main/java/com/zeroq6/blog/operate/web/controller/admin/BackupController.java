package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.operate.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @author
 * @date 2018/8/16
 */
@Controller()
@RequestMapping("/admin")
public class BackupController extends BaseController{


    @Autowired
    private BackupService backupService;


    @RequestMapping(value = "/backup")
    public String receive(HttpServletResponse response) throws Exception {
        try{
            boolean success = backupService.backup();
            outJson(response, success + "");
        }catch (Exception e){
            logger.error("接收邮件异常", e);
            outJson(response, "error: " + e.getMessage());
        }
        return null;
    }
}





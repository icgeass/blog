package com.zeroq6.blog.operate.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.common.mail.ReceiveMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Controller()
@RequestMapping("/mail")
public class MailReceiveController extends BaseController{


    @Autowired
    private DictManager dictManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/receive")
    public String receive(HttpServletResponse response) throws Exception {
        try{
            DictDomain dictDomain = dictManager.getDictByTypeAndKey(EmDictDictType.YOUXIANG.value(), "mailConfig");
            JSONObject config = JSON.parseObject(dictDomain.getDictValue());
            String content = ReceiveMail.receiveEmail(config.getString("mailPop3Host"),
                    config.getString("mailStoreType"), config.getString("mailUser"),
                    config.getString("mailPassword"), config.getString("mailTitle"),
                    config.getString("mailFrom"));
            content = null == config ? "没有收到邮件，请联系管理员" : content;
            outJson(response, content);
        }catch (Exception e){
            logger.error("接收邮件异常", e);
            outJson(response, "error: " + e.getMessage());
        }
        return null;
    }

}

package com.zeroq6.blog.common.base;

import com.alibaba.fastjson.JSON;
import com.zeroq6.common.web.CustomDateEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public abstract class BaseController {


    public final static String NAME_MENU = "menu";

    public final static String NAME_CATEGORY_TITLE = "categoryTitle";

    public final Logger logger = LoggerFactory.getLogger(getClass());

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }


    protected void outJson(HttpServletResponse response, Object object) {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        String json = object instanceof String ? String.valueOf(object) : JSON.toJSONString(object);
        try {
            PrintWriter out = response.getWriter();
            out.print(json);
            out.close();
        } catch (IOException e) {
            logger.error("向页面输出json发生错误, " + json, e);
        }
    }

    protected String redirectIndex(){
        return "redirect:/";
    }

}

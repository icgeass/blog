package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.operate.service.DictService;
import com.zeroq6.blog.operate.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by yuuki asuna on 2017/5/24.
 */

@Controller
@RequestMapping("/history")
public class HistoryController extends BaseController{

    @Autowired
    private PostService postService;

    @Autowired
    private DictService dictService;



    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute(NAME_MENU, "history");
        model.addAttribute(NAME_CATEGORY_TITLE, "历史");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(Model view){
        try {
            BaseResponse<List<DictDomain>> result = dictService.getHistory();
            if(result.isSuccess()){
                view.addAttribute("historyList", result.getBody());
                return "/history";
            }
        } catch (Exception e) {
            logger.error("历史页面异常", e);
        }
        return redirectIndex();

    }
}

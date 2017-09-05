package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.service.DictService;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.common.base.BaseResponse;
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
        model.addAttribute("menu", "history");
        model.addAttribute("categoryTitle", "历史");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }


    @RequestMapping
    public String index(Model view){
        BaseResponse<List<DictDomain>> result = dictService.getHistory();
        if(result.isSuccess()){
            view.addAttribute("historyList", result.getBody());
            return "/history";
        }
        return null;

    }
}

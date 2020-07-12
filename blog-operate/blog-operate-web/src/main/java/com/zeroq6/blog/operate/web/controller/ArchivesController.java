package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by yuuki asuna on 2017/5/24.
 */
@Controller
@RequestMapping("/archives")
public class ArchivesController extends BaseController {


    @Autowired
    private PostService postService;


    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute(NAME_MENU, "archives");
        model.addAttribute(NAME_CATEGORY_TITLE, "归档");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }

    @RequestMapping
    public String index(Model view) {
        try {
            BaseResponse<Map<String, List<PostDomain>>> result = postService.getArchiveList(null, null);
            if (result.isSuccess()) {
                view.addAttribute("archiveMapList", result.getBody());
                return "/archives";
            }
        } catch (Exception e) {
            logger.error("归档页面异常", e);
        }
        return redirectIndex();
    }


}

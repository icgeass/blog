package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.blog.common.base.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by yuuki asuna on 2017/5/24.
 */
@Controller
@RequestMapping("/tags")
public class TagsController extends BaseController {


    @Autowired
    private PostService postService;

    @Autowired
    private DictManager dictManager;

    @ModelAttribute
    public void loadState(Model model) {
        model.addAttribute(NAME_MENU, "archives");
        model.addAllAttributes(postService.getSidebarInfo().getBody());
    }

    @RequestMapping(value = "/{tag}")
    public String index(@PathVariable String tag, Model view) {
        try {
            if (StringUtils.isBlank(tag)) {
                return redirectIndex();
            }
            BaseResponse<Map<String, List<PostDomain>>> result = postService.getArchiveList(null, tag);
            if (result.isSuccess()) {
                DictDomain dictDomain = dictManager.getDictByTypeAndKey(EmDictDictType.BIAOQIAN.value(), tag);
                view.addAttribute(NAME_CATEGORY_TITLE, dictDomain.getDictValue());
                view.addAttribute("archiveMapList", result.getBody());
                view.addAttribute("classify", dictDomain.getDictValue());
                return "/archives";
            }
        } catch (Exception e) {
            logger.error("标签文章列表异常: " + tag, e);
        }
        return redirectIndex();
    }
}

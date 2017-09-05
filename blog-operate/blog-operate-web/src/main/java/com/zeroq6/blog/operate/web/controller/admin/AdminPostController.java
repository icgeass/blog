package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.enums.field.EmDictDictType;
import com.zeroq6.blog.common.utils.PostUtils;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.manager.PostManager;
import com.zeroq6.blog.operate.service.PostService;
import com.zeroq6.common.base.BaseResponse;
import com.zeroq6.common.base.Page;
import com.zeroq6.common.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yuuki asuna on 2017/8/2.
 */
@Controller
@RequestMapping("/admin/post")
public class AdminPostController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PostService postService;

    @Autowired
    private PostManager postManager;

    @Autowired
    private DictManager dictManager;

    @RequestMapping("")
    public String list(PostDomain postDomain, Page<PostDomain> page, Model view) {
        postService.selectPage(postDomain, page);
        for(PostDomain item : page.getData()){
            item.put("summary", PostUtils.getHtmlTextSubstring(item, 50));
        }
        view.addAttribute("page", page);
        view.addAttribute("postDomain", postDomain);
        return "/admin/post/postList";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model view) {
        if (id > 0) {
            BaseResponse<Map<String, Object>> response = postService.show(id);
            if (response.isSuccess()) {
                view.addAllAttributes(response.getBody());
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }
        view.addAttribute("tagsAll", dictManager.getDictByType(EmDictDictType.BIAOQIAN.value()));
        view.addAttribute("categoryAll", dictManager.getDictByType(EmDictDictType.FENLEI.value()));
        return "/admin/post/postEdit";
    }

    @RequestMapping("/save")
    public String save(PostDomain postDomain, Model view) {
        List<String> tags = Arrays.asList(MyStringUtils.toStringArray(postDomain.get("tags")));
        String category = (String)postDomain.get("category");
        if(null == postDomain.getId() || postDomain.getId() <= 0L){
            postService.addPost(postDomain, tags, category);
        }else{
            postService.editPost(postDomain, tags, category);
        }
        return "redirect:/admin/post";
    }


    @RequestMapping("/delete/{id}")
    public String del(@PathVariable Long id, Model view) {
        postManager.deleteById(id);
        return "redirect:/admin/post";
    }



}

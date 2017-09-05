package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.service.DictService;
import com.zeroq6.common.base.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yuuki asuna on 2017/8/2.
 */

@Controller
@RequestMapping("/admin/dict")
public class AdminDictController {



    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DictService dictService;


    @Autowired
    private DictManager dictManager;

    @RequestMapping("")
    public String list(DictDomain dictDomain, Page<DictDomain> page, Model view) {
        dictService.selectPage(dictDomain, page);
        view.addAttribute("page", page);
        view.addAttribute("dictDomain", dictDomain);
        return "/admin/dict/dictList";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model view) {
        view.addAttribute("dict", dictService.selectOne(new DictDomain().setId(id), true));
        return "/admin/dict/dictEdit";
    }

    @RequestMapping("/save")
    public String save(DictDomain dictDomain, Model view) {
        dictManager.saveOrUpdate(dictDomain);
        return "redirect:/admin/dict";
    }


    @RequestMapping("/delete/{id}")
    public String del(@PathVariable Long id, Model view) {
        dictService.disableByKey(id);
        return "redirect:/admin/dict";
    }
}

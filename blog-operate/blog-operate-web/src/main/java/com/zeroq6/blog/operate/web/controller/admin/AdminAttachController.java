package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.domain.AttachDomain;
import com.zeroq6.blog.operate.manager.AttachManager;
import com.zeroq6.blog.operate.service.AttachService;
import com.zeroq6.common.base.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yuuki asuna on 2017/8/2.
 */

@Controller
@RequestMapping("/admin/attach")
public class AdminAttachController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AttachService attachService;

    @Autowired
    private AttachManager attachManager;

    @RequestMapping("")
    public String list(AttachDomain attachDomain, Page<AttachDomain> page, Model view) {
        attachService.selectPage(attachDomain, page);
        view.addAttribute("page", page);
        view.addAttribute("attachDomain", attachDomain);
        return "/admin/attach/attachList";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model view) {
        return "/admin/attach/attachEdit";
    }

    @RequestMapping("/save")
    public String save(@RequestParam(value = "file", required = false) MultipartFile[] files) {
        attachManager.save(files);
        return "redirect:/admin/attach";
    }


    @RequestMapping("/delete/{id}")
    public String del(@PathVariable Long id, Model view) {
        attachManager.deleteById(id);
        return "redirect:/admin/attach";
    }

}

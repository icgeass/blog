package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.domain.AttachDomain;
import com.zeroq6.blog.operate.manager.AttachManager;
import com.zeroq6.blog.operate.service.AttachService;
import com.zeroq6.common.web.DownloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by yuuki asuna on 2017/8/10.
 */
@Controller
@RequestMapping("attach")
public class AttachController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AttachService attachService;

    @Autowired
    private AttachManager attachManager;


    /**
     *
     * @param response
     * @param md5
     * @param name
     */
    @RequestMapping(value = "/download/{md5}/{name}", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, @PathVariable("md5") String md5, @PathVariable("name") String name) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File file = null;
            AttachDomain attachDomain = attachService.selectOne(new AttachDomain().setMd5(md5), true);
            if (null != attachDomain) {
                file = new File(attachManager.getUploadPath() + File.separator + attachDomain.getName());
            }
            if (null == attachDomain) {
                throw new RuntimeException("Not Found");
            }
            DownloadUtils.download(response, file, null);
        } catch (Exception e) {
            logger.info("下载失败", e);
        }
    }

}

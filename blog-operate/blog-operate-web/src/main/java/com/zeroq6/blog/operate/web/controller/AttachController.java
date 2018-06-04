package com.zeroq6.blog.operate.web.controller;

import com.zeroq6.blog.common.domain.AttachDomain;
import com.zeroq6.blog.operate.manager.AttachManager;
import com.zeroq6.blog.operate.service.AttachService;
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
     * http://websystique.com/springmvc/spring-mvc-4-file-download-example/
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
            if (null == attachDomain || !file.exists()) {
                throw new RuntimeException("Not Found");
            }
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // 直接下载
            }
            response.setContentType(mimeType);
            // inline 浏览器打开, attachment 下载框下载
            response.setHeader("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(attachDomain.getName(), "utf-8") + "\"");
            response.setContentLength((int) file.length());
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = response.getOutputStream();
            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, outputStream);

        } catch (Exception e) {
            logger.info("下载失败", e);
            try {
                if (null != outputStream) {
                    outputStream = response.getOutputStream();
                }
                outputStream.write(e.getMessage().getBytes(Charset.forName("UTF-8")));
            } catch (Exception ee) {
                logger.error("输出错误信息到前端异常", ee);
            }
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    logger.error("关闭输出流异常", e);
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("关闭输入流异常", e);
                }
            }
        }
    }

}

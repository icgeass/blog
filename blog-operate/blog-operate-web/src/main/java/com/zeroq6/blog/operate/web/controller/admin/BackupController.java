package com.zeroq6.blog.operate.web.controller.admin;

import com.zeroq6.blog.common.base.BaseController;
import com.zeroq6.blog.operate.service.BackupService;
import com.zeroq6.common.web.DownloadUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author
 * @date 2018/8/16
 */
@Controller()
@RequestMapping("/admin/backup")
public class BackupController extends BaseController {


    @Autowired
    private BackupService backupService;


    @RequestMapping(value = "/mail")
    public String receive(HttpServletResponse response) throws Exception {
        try {
            File zipFile = backupService.backup(true, true);
            outJson(response, (zipFile != null) + "");
        } catch (Exception e) {
            logger.error("备份到邮件异常", e);
            outJson(response, "error: " + e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/download")
    public String download(HttpServletResponse response) throws Exception {
        File zipFile = null;
        try {
            zipFile = backupService.backup(false, false);
            DownloadUtils.download(response, zipFile, null);
        } catch (Exception e) {
            logger.error("备份下载异常", e);
            outJson(response, "error: " + e.getMessage());
        } finally {
            if (null != zipFile) {
                FileUtils.deleteQuietly(zipFile);
                zipFile = null;
            }
        }
        return null;
    }

    @RequestMapping(value = "/markdown")
    public String downloadMarkdown(HttpServletResponse response) throws Exception {
        File zipFile = null;
        try {
            zipFile = backupService.downloadMarkdown();
            if (null != zipFile) {
                DownloadUtils.download(response, zipFile, null);
            }
        } catch (Exception e) {
            logger.error("备份下载Markdown异常", e);
            outJson(response, "error: " + e.getMessage());
        } finally {
            if (null != zipFile) {
                FileUtils.deleteQuietly(zipFile);
                zipFile = null;
            }
        }
        return null;
    }
}





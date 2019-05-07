package com.zeroq6.common.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class DownloadUtils {
    
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DownloadUtils.class);

    /**
     * http://websystique.com/springmvc/spring-mvc-4-file-download-example/
     * @param response
     * @param file
     */
    public static void download(HttpServletResponse response, File file, String downloadFileName){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                throw new RuntimeException("Not Found");
            }
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // 直接下载
            }
            response.setContentType(mimeType);
            // inline 浏览器打开, attachment 下载框下载
            String fileName = StringUtils.isBlank(downloadFileName) ? file.getName() : downloadFileName;
            response.setHeader("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"");
            response.setContentLength((int) file.length());
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = response.getOutputStream();
            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, outputStream);

        } catch (Exception e) {
            LOGGER.info("下载失败", e);
            try {
                if (null == outputStream) {
                    outputStream = response.getOutputStream();
                }
                outputStream.write(e.getMessage().getBytes(Charset.forName("UTF-8")));
            } catch (Exception ee) {
                LOGGER.error("输出错误信息到前端异常", ee);
            }
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    LOGGER.error("关闭输出流异常", e);
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOGGER.error("关闭输入流异常", e);
                }
            }
        }
    }
}

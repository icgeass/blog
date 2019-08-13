package com.zeroq6.common.web;

import com.zeroq6.common.utils.CloseUtils;
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
     *
     * @param response
     * @param file
     */
    public static void download(HttpServletResponse response, File file, String downloadFileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (null == file) {
                throw new RuntimeException("The file record NOT in our database");
            }
            if (!file.exists()) {
                throw new RuntimeException("The file to download not exist on our disk");
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
                outputStream.write(("Exception: " + e.getMessage()).getBytes(Charset.forName("UTF-8")));
            } catch (Exception ee) {
                LOGGER.error("输出错误信息到前端异常", ee);
            }
        } finally {
            CloseUtils.closeNoException(outputStream);
            CloseUtils.closeNoException(inputStream);
        }
    }
}

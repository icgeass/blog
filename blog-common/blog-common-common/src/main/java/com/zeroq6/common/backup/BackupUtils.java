package com.zeroq6.common.backup;

import com.alibaba.fastjson.JSON;
import com.zeroq6.common.utils.CloseUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author
 * @date 2018/8/14
 */
public class BackupUtils {


    private final static Logger LOGGER = LoggerFactory.getLogger(BackupUtils.class);


    public static void main(String[] args) {
        exec(new HashMap<String, String>() {
            {
                put("cmd.txt", "ipconfig");
            }
        });
    }


    public static void zipFolders(File zipFile, List<String> folders) {
        try {
            if (null == folders || folders.isEmpty()) {
                throw new RuntimeException("folders不能为空");
            }
            // 获取文件，去重
            List<String> absPathList = new ArrayList<String>();
            Collection<File> files = new LinkedList<File>();
            for (String folder : folders) {
                File f = new File(folder);
                if (!f.exists()) {
                    throw new IllegalArgumentException("文件（夹）不存在: " + f.getAbsolutePath());
                }
                if (f.isFile()) {
                    if (!absPathList.contains(f.getCanonicalPath())) {
                        files.add(f);
                        absPathList.add(f.getCanonicalPath());
                    }
                } else if (f.isDirectory()) {
                    for (File fileInFolder : FileUtils.listFiles(f, null, true)) {
                        if (!absPathList.contains(fileInFolder.getCanonicalPath())) {
                            files.add(fileInFolder);
                            absPathList.add(fileInFolder.getCanonicalPath());
                        }
                    }

                } else {
                    throw new RuntimeException("非法路径: " + f.getAbsolutePath());
                }
            }
            LOGGER.info("当前备份文件列表：{}", JSON.toJSONString(absPathList));
            OutputStream out = null;
            ArchiveOutputStream os = null;
            InputStream is = null;

            try {
                File parent = zipFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                out = new FileOutputStream(zipFile);

                os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);
                Iterator<File> iterator = files.iterator();

                while (iterator.hasNext()) {
                    File f = iterator.next();

                    String path = f.getAbsolutePath();

                    ///
                    os.putArchiveEntry(new ZipArchiveEntry(path));
                    is = new FileInputStream(f);
                    IOUtils.copy(new FileInputStream(f), os);
                    os.closeArchiveEntry();

                }
                out.flush();
            } finally {
                CloseUtils.closeSilent(os);
                CloseUtils.closeSilent(out);
                CloseUtils.closeSilent(is);
            }


        } catch (Exception e) {
            LOGGER.error("压缩文件异常", e);
            throw new RuntimeException(e);
        }

    }


    public static Map<String, String> exec(Map<String, String> cmdMap) {
        Map<String, String> result = null;
        try {
            result = new HashMap<String, String>();
            for (Map.Entry<String, String> item : cmdMap.entrySet()) {
                if (StringUtils.isBlank(item.getKey())) {
                    throw new RuntimeException("key不能为空");
                }
                Process process = Runtime.getRuntime().exec(item.getValue());

                //
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader input = null;
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    is = process.getInputStream();
                    isr = new InputStreamReader(is);
                    input = new BufferedReader(isr);
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        stringBuilder.append(line).append("\r\n");
                    }
                } finally {
                    CloseUtils.closeSilent(input);
                    CloseUtils.closeSilent(isr);
                    CloseUtils.closeSilent(is);
                }
                int exitValue = process.waitFor();
                if (0 != exitValue) {
                    throw new RuntimeException("命令执行exitValue不为0，" + item.getKey() + ":" + item.getValue());
                }
                result.put(item.getKey(), stringBuilder.toString());
            }
        } catch (Throwable e) {
            LOGGER.error("命令执行失败", e);
            throw new RuntimeException(e);
        }
        return result;
    }


}

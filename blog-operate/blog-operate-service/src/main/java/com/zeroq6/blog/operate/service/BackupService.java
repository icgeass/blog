package com.zeroq6.blog.operate.service;

import com.alibaba.fastjson.JSON;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.domain.BackupConfigDomain;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.manager.velocity.MailConfigManager;
import com.zeroq6.common.backup.BackupUtils;
import com.zeroq6.common.mail.MailSender;
import com.zeroq6.common.mail.MailSenderConfig;
import com.zeroq6.common.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 * @date 2018/8/14
 */

@Service
public class BackupService {

    @Autowired
    public DictManager dictManager;

    @Autowired
    private MailConfigManager mailConfigManager;

    @Value("${sys.config.key.backup}")
    private String sysConfigKeyBackup;

    @Value("${project.domain}")
    private String projectDoamin;


    private long zipFileSize = -1;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final String tmpDir = System.getProperty("java.io.tmpdir");

    public void backup() throws Exception {
        try {
            backup(true, true);
        } catch (Exception e) {
            logger.error("备份异常", e);
        }
    }


    /**
     * 返回zip 文件
     *
     * @return
     * @throws Exception
     */
    public File backup(boolean sendMail, boolean deleteZipFile) throws Exception {

        String dataString = new SimpleDateFormat("yyyyMMdd").format(new Date());

        MailSenderConfig mailSenderConfig = mailConfigManager.getMailSenderConfig();
        Properties mailSendProperties = mailConfigManager.getProperties();


        File zipFile = null;
        try {
            logger.info("开始备份");
            if (null == mailSenderConfig || null == mailSendProperties) {
                sendMail = false;
                logger.warn("mailSenderConfig或mailSendProperties为null");
            }


            DictDomain dictDomain = dictManager.selectOne(new DictDomain().setDictType(EmDictDictType.XI_TONG_PEIZHI.value()).setDictKey(sysConfigKeyBackup), true);
            if (null == dictDomain || StringUtils.isBlank(dictDomain.getDictValue())) {
                logger.info("无备份配置, 执行结束");
                return null;
            }
            BackupConfigDomain backupConfigDomain = JSON.parseObject(dictDomain.getDictValue(), BackupConfigDomain.class);
            logger.info("备份配置: " + JsonUtils.toJSONString(backupConfigDomain));


            List<String> folders = backupConfigDomain.getBackupFolders();


            // 执行命令并写入文件
            Map<String, String> execResultMap = BackupUtils.exec(backupConfigDomain.getCmdMap());

            File parent = new File(backupConfigDomain.getBaseDir() + File.separator + dataString);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            for (Map.Entry<String, String> item : execResultMap.entrySet()) {
                File file = new File(parent, item.getKey());
                FileUtils.write(file, item.getValue(), "UTF-8");
                folders.add(file.getAbsolutePath());
            }

            // 备份文件夹
            zipFile = new File(tmpDir + File.separator + "backup_" + projectDoamin + "_" + dataString + ".zip");
            BackupUtils.zipFolders(zipFile, folders);


            if (sendMail) {
                if (zipFileSize != zipFile.length()) {
                    MailSender.sendMail(mailSendProperties, mailSenderConfig.getFromAddress(), mailSenderConfig.getPassword(),
                            "【站点备份-成功】" + dataString, mailSenderConfig.getToAddress(), "备份成功" + dataString,
                            new File[]{zipFile}, null, null);
                    zipFileSize = zipFile.length();
                } else {
                    logger.warn("原备份文件大小和现备份文件大小一致，跳过备份，sizeInBytes={}({})", zipFileSize, com.zeroq6.blog.common.utils.FileUtils.getReadableSize(zipFileSize));
                    FileUtils.deleteDirectory(parent);
                }

            }
            return zipFile;
        } catch (Exception e) {
            logger.error("备份异常", e);
            if (sendMail) {
                try {
                    MailSender.sendMail(mailSendProperties, mailSenderConfig.getFromAddress(), mailSenderConfig.getPassword(),
                            "【站点备份-异常】" + dataString, mailSenderConfig.getToAddress(), e.getMessage(),
                            null, null, null);

                } catch (Exception ee) {
                    logger.error("发送备份异常提醒邮件-异常", e);
                }

            }
        } finally {
            if (null != zipFile && deleteZipFile) {
                FileUtils.deleteQuietly(zipFile);
                zipFile = null;
            }
            logger.info("备份结束");
        }
        return null;
    }


}

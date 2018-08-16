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
import com.zeroq6.common.utils.ExceptionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void backup() throws Exception {

        String dataString = new SimpleDateFormat("yyyyMMdd").format(new Date());

        MailSenderConfig mailSenderConfig = mailConfigManager.getMailSenderConfig();
        try {
            logger.info("开始备份");
            DictDomain dictDomain = dictManager.selectOne(new DictDomain().setDictType(EmDictDictType.XI_TONG_PEIZHI.value()).setDictKey(sysConfigKeyBackup), true);
            if (null == dictDomain || StringUtils.isBlank(dictDomain.getDictValue())) {
                logger.info("无备份配置, 执行结束");
                return;
            }
            BackupConfigDomain backupConfigDomain = JSON.parseObject(dictDomain.getDictValue(), BackupConfigDomain.class);
            logger.info("备份配置: " + JSON.toJSONString(backupConfigDomain));


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
            File zipFile = new File(parent.getCanonicalPath() + File.separator + "重要备份_" + dataString + ".zip");
            BackupUtils.zipFolders(zipFile, folders);


            MailSender.sendMail(mailConfigManager.getProperties(), mailSenderConfig.getFromAddress(), mailSenderConfig.getPassword(),
                    "【站点备份-成功】重要" + dataString, mailSenderConfig.getToAddress(), "备份成功" + dataString,
                    new File[]{zipFile}, null, null);

        } catch (Exception e) {
            logger.error("备份异常", e);
            MailSender.sendMail(mailConfigManager.getProperties(), mailSenderConfig.getFromAddress(), mailSenderConfig.getPassword(),
                    "【站点备份-异常】" + dataString, mailSenderConfig.getToAddress(), e.getMessage(),
                    null, null, null);
        } finally {
            logger.info("备份结束");
        }
    }


}

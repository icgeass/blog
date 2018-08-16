package com.zeroq6.blog.operate.manager.velocity;

import com.alibaba.fastjson.JSON;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.common.mail.MailSenderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author
 * @date 2018/8/15
 */
@Service
public class MailConfigManager implements InitializingBean {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DictManager dictManager;

    @Value("${sys.config.key.mail_send}")
    private String sysConfigKeyMailSend;


    private MailSenderConfig mailSenderConfig;

    private Properties properties  = new Properties();

    public Properties getProperties() {
        afterPropertiesSet();
        return properties;
    }

    public MailSenderConfig getMailSenderConfig() {
        afterPropertiesSet();
        return mailSenderConfig;
    }

    @Override
    public void afterPropertiesSet(){

        try {
            logger.info("开始获取邮件配置");

            DictDomain dictDomain = dictManager.selectOne(new DictDomain().setDictType(EmDictDictType.XI_TONG_PEIZHI.value()).setDictKey(sysConfigKeyMailSend), true);

            if (null == dictDomain) {
                logger.warn("无法查找邮件发送配置");
            }
            //
            mailSenderConfig = JSON.parseObject(dictDomain.getDictValue(), MailSenderConfig.class);


            // 开启debug调试 ，打印信息
            properties.setProperty("mail.debug", mailSenderConfig.getDubug());
            // 发送服务器需要身份验证
            properties.setProperty("mail.smtp.auth", mailSenderConfig.getSmtpAuth());
            // 发送服务器端口，可以不设置，默认是25
            properties.setProperty("mail.smtp.port", mailSenderConfig.getSmtpPort());
            // 发送邮件协议名称
            properties.setProperty("mail.transport.protocol", mailSenderConfig.getTransportProtocol());
            // 设置邮件服务器主机名
            properties.setProperty("mail.host", mailSenderConfig.getHost());




            logger.info("获取邮件配置成功，" + JSON.toJSONString(mailSenderConfig));
        } catch (Exception e) {
            logger.error("获取邮件配置异常", e);
        }

    }


}

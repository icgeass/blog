package com.zeroq6.common.mail;

import com.zeroq6.common.utils.CloseUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author http://www.cnblogs.com/h--d/p/6138810.html
 * <p>
 * https://www.cnblogs.com/h--d/p/6138900.html
 * @date 2018/8/14
 */
public class MailSender {


    private final static Logger LOGGER = LoggerFactory.getLogger(MailSender.class);


    private final static String HTML_CONTENT_TYPE = "text/html;charset=utf-8";


    /**
     * http://blog.itpub.net/15182208/viewspace-730172/
     * <p>
     * mail的content为MimeMultipart
     * 一个MimeMultipart有一个或多个MimeBodyPart
     * MimeBodyPart的content为MimeMultipart 或简单资源
     * <p>
     * multipart/mixed：附件。
     * <p>
     * multipart/related：内嵌资源。
     * <p>
     * multipart/alternative：纯文本与超文本共存。
     *
     * @param subject
     * @param fromAddress    发件人
     * @param toAddress      收件人
     * @param html           邮件body
     * @param attachFiles    附件，可为null
     * @param imgLocationMap html引用的本地图片，可为null
     * @param writeTo        写入的本地文件，可为null
     * @return
     */
    public static void sendMail(Properties properties, String fromAddress, String password, String subject, String toAddress, String html, File[] attachFiles, Map<String, File> imgLocationMap, File writeTo) {
        MimeMessage mail = null;
        OutputStream os = null;
        try {
            String username = fromAddress.substring(0, fromAddress.indexOf("@"));
            // 环境信息
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 在session中设置账户信息，Transport发送邮件时会使用
                    return new PasswordAuthentication(username, password);
                }
            });

            //邮件
            mail = new MimeMessage(session);
            //设置主题
            mail.setSubject(subject);
            //发件人，注意中文的处理
            mail.setFrom(new InternetAddress(String.format("\"%s\"<%s>", MimeUtility.encodeText(trimNull(username)), fromAddress)));
            //设置邮件回复人
            mail.setReplyTo(new Address[]{new InternetAddress(toAddress)});
//            mail.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(fromAddress));
            //整封邮件的MINE消息体
            MimeMultipart mainMimeMultipart = new MimeMultipart("mixed"); //混合的组合关系
            //设置邮件的Multipart
            mail.setContent(mainMimeMultipart);

            if (null != attachFiles && attachFiles.length != 0) {
                for (File file : attachFiles) {
                    MimeBodyPart attach = new MimeBodyPart();
                    //把文件，添加到附件1中
                    //数据源
                    DataSource fileDataSource = new FileDataSource(file);
                    //数据处理器
                    DataHandler dataHandler = new DataHandler(fileDataSource);
                    //设置第一个附件的数据
                    attach.setDataHandler(dataHandler);
                    //设置第一个附件的文件名
                    attach.setFileName(MimeUtility.encodeText(trimNull(file.getName())));
                    // 为邮件的Multipart添加BodyPart附件
                    mainMimeMultipart.addBodyPart(attach);
                }
            }
            // 为邮件的Multipart添加正文html
            MimeBodyPart bodyPart = new MimeBodyPart();
            mainMimeMultipart.addBodyPart(bodyPart);
            if (null != imgLocationMap && !imgLocationMap.isEmpty()) {
                // 如果有图片，则htmlBodyPart的content使用MimeMultipart包含多个bodyPart
                MimeMultipart bodyMultipart = new MimeMultipart("related");
                //设置内容为正文
                bodyPart.setContent(bodyMultipart);

                //
                MimeBodyPart htmlPart = new MimeBodyPart();
                bodyMultipart.addBodyPart(htmlPart);
                htmlPart.setContent(html, HTML_CONTENT_TYPE);


                //把文件，添加到图片中
                for (Map.Entry<String, File> item : imgLocationMap.entrySet()) {
                    File file = item.getValue();
                    if (null == file || !file.exists()) {
                        throw new RuntimeException("引用图片文件不存在：" + file);
                    }
                    MimeBodyPart imgPart = new MimeBodyPart();
                    bodyMultipart.addBodyPart(imgPart);

                    DataSource imgDS = new FileDataSource(file);
                    DataHandler imgDH = new DataHandler(imgDS);
                    imgPart.setDataHandler(imgDH);
                    //说明html中的img标签的src，引用的是此图片
                    imgPart.setHeader("Content-Location", item.getKey());
                    imgPart.setFileName(MimeUtility.encodeText(trimNull(file.getName())));
                }
            } else {
                bodyPart.setContent(html, HTML_CONTENT_TYPE);
            }

            // 生成文件邮件
            mail.saveChanges();

            if (null != writeTo) {
                File parent = writeTo.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                os = new FileOutputStream(writeTo);
                mail.writeTo(os);
            }
            Transport.send(mail, InternetAddress.parse(toAddress));
        } catch (Exception e) {
            LOGGER.error("邮件发送异常", e);
            throw new RuntimeException(e);
        } finally {
            CloseUtils.closeSilent(os);
        }

    }

    private static String trimNull(String s) {
        return StringUtils.isBlank(s) ? "" : s.trim();
    }


}

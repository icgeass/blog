package com.zeroq6.common.mail;

import com.sun.mail.pop3.POP3Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Properties;

/**
 * @author
 * @date 2018/7/11
 * <p>
 * http://www.avajava.com/tutorials/lessons/how-do-i-receive-email-in-java.html
 */
public class ReceiveMail {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReceiveMail.class);


    /**
     * 获得最新一封邮件
     *
     * @param pop3Host
     * @param storeType
     * @param user
     * @param password
     * @param title
     * @param from
     * @return
     */
    public static String receiveEmail(String pop3Host, String storeType, String user, String password, String title, String from) {
        POP3Store emailStore = null;
        Folder emailFolder = null;
        String content = null;
        try {
            Properties properties = new Properties();
            properties.put("mail.pop3.host", pop3Host);
            Session emailSession = Session.getDefaultInstance(properties);

            emailStore = (POP3Store) emailSession.getStore(storeType);
            emailStore.connect(user, password);

            emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            int count = emailFolder.getMessageCount();
            Message[] messages = emailFolder.getMessages(count - 9, count);
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[messages.length - 1 - i];
                if (title.equals(message.getSubject()) && message.getFrom()[0].toString().contains(from)) {
                    content = message.getContent().toString();
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("接收邮件异常", e);
        } finally {
            try {
                if (null != emailFolder) {
                    emailFolder.close(false);
                }
            } catch (Exception e) {
                LOGGER.error("关闭Folder异常", e);
            }
            try {
                if (null != emailStore) {
                    emailStore.close();
                }
            } catch (Exception e) {
                LOGGER.error("关闭POP3Store异常", e);
            }
        }
        return content;

    }

}
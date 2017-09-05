package com.zeroq6.blog.operate.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class AppListener implements ServletContextListener, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("contextInitialized................");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("contextDestroyed................");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("afterPropertiesSet................");

    }
}

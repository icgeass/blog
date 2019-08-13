package com.zeroq6.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * @date 2018/8/15
 */
public class CloseUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(CloseUtils.class);



    public static void closeNoException(AutoCloseable autoCloseable) {
        try {
            if (null != autoCloseable) {
                autoCloseable.close();
            }
        } catch (Exception e) {
            LOGGER.error("关闭" + autoCloseable.getClass().getSimpleName() + "异常", e);
        }
    }
}

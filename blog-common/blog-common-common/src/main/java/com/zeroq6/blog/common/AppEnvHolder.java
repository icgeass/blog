package com.zeroq6.blog.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppEnvHolder implements InitializingBean {

    @Value("${appEnv}")
    private String appEnv;

    @Value("${developEnv}")
    private String developEnv;

    @Value("${testEnv}")
    private String testEnv;

    @Value("${productionEnv}")
    private String productionEnv;


    private static String _appEnv;

    private static String _developEnv;


    private static String _testEnv;


    private static String _productionEnv;


    public static boolean isTestEnv() {
        return _testEnv.equals(_appEnv);
    }

    public static boolean isDevelopEnv() {
        return _developEnv.equals(_appEnv);
    }


    public static boolean isProductionEnv() {
        return _productionEnv.equals(_appEnv);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        _appEnv = appEnv;
        _developEnv = developEnv;
        _testEnv = testEnv;
        _productionEnv = productionEnv;
    }
}

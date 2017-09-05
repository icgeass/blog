package com.zeroq6.blog.common.enums;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class EnumUtils {

    private final static Logger logger = LoggerFactory.getLogger(EnumUtils.class);

    private final static String BASE_PACKAGE_SCAN = "com.zeroq6";

    private final static Map<String, EnumApi[]> classNameEnumValues = getEnumValuesByBasePackage(BASE_PACKAGE_SCAN);


    public static EnumApi[] values(String classSimpleName) {
        return classNameEnumValues.get(classSimpleName);
    }

    /**
     * http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     *
     * @param basePackage
     * @return
     */
    private static Map<String, EnumApi[]> getEnumValuesByBasePackage(String basePackage) {
        Map<String, EnumApi[]> re = new HashMap<String, EnumApi[]>();
        try {
            Reflections reflections = new Reflections(basePackage, new SubTypesScanner(false));
            Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
            for (Class clz : allClasses) {
                if (clz.isEnum() && EnumApi.class.isAssignableFrom(clz)) {
                    re.put(clz.getSimpleName(), (EnumApi[]) (clz.getMethod("values").invoke(null)));
                }
            }
        } catch (Exception e) {
            logger.error("初始枚举values错误", e);
        }
        return re;
    }
}
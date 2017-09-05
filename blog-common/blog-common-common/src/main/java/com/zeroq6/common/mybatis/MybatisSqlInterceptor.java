package com.zeroq6.common.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class MybatisSqlInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String PRODUCTION_ENV_STRING = "production";

    private Properties properties;

    @Value("${sql.maxTimeMills}")
    private int sqlMaxTimeMills;

    @Value("${project.enviroment}")
    private String env;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object returnValue = null;
        long begin = System.currentTimeMillis();
        Throwable throwable = null;
        try{
            returnValue = invocation.proceed();
        }catch (Throwable t){
            throwable = t;
            throw t;
        }finally {
            try{
                doIt(invocation, begin, System.currentTimeMillis(), throwable);
            }catch (Exception e){
                // todo ignore or do something
            }
        }
        return returnValue;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public void doIt(Invocation invocation, long begin, long end, Throwable throwable){
        long time = end - begin;
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        String sqlType = mappedStatement.getSqlCommandType().name();
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        String sql = new StringBuffer().append(sqlId).append(": ").append(sqlType).append(": ").append(removeComment(getSql(configuration, boundSql))).toString();
        if(null != throwable || time >= sqlMaxTimeMills || !PRODUCTION_ENV_STRING.equals(env)){
            // todo 报警
            logger.info("sql执行{}, 耗时{}毫秒, {}", throwable == null ? "成功" : "失败, " + throwable.getCause().getMessage(), String.valueOf(time), sql);
        }
        // todo 写入sql记录
    }


    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format((Date)obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    private static String removeComment(String string){
        if(null == string){
            return null;
        }
        StringBuffer sb = new StringBuffer(string);
        String begin = "/*";
        String end = "*/";
        int beginIndex = -1;
        int endIndex = -1;
        while ((beginIndex = sb.indexOf(begin)) != -1){
            if((endIndex = sb.indexOf(end, beginIndex)) != -1){
                sb.delete(beginIndex, endIndex + end.length());
            }else{
                break;
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    private String getSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

}

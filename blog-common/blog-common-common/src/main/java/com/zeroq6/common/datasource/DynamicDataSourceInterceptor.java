package com.zeroq6.common.datasource;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class DynamicDataSourceInterceptor implements Interceptor {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Map<String, EmDataSourceType> cacheMap = new ConcurrentHashMap<String, EmDataSourceType>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) objects[0];
            EmDataSourceType dataSourceType = null;
            String sqlId = mappedStatement.getId();
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if ((dataSourceType = cacheMap.get(sqlId)) == null) {
                //读方法
                if (sqlCommandType.equals(SqlCommandType.SELECT)) {
                    //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                    if (sqlId.contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                        dataSourceType = EmDataSourceType.MASTER;
                    } else {
                        dataSourceType = EmDataSourceType.SLAVE;
                    }
                } else {
                    dataSourceType = EmDataSourceType.MASTER;
                }
                logger.warn("设置sqlId对应数据源缓存, sqlId: {}, sqlCommandType: {}, dataSourceType: {}", mappedStatement.getId(), sqlCommandType.name(), dataSourceType.name());
                cacheMap.put(mappedStatement.getId(), dataSourceType);
            }
            DataSourceHolder.setDataSource(dataSourceType);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }
}
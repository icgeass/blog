package com.zeroq6.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 只读事务到SLAVE库，读写事务到MASTER库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {

        //设置数据源
        // isSynchronizationActive为true时不会调用doBegin，所以可以不用加判断
        // 事务和连接（当前线程）绑定，事务readOnly会以事务的入口方法为准，调用入口方法时当前线程数据源已确定
        // EmDataSourceType dataSourceType = DataSourceHolder.getDataSource();
        // if(null == dataSourceType || !TransactionSynchronizationManager.isSynchronizationActive()){
        if (definition.isReadOnly()) {
            DataSourceHolder.setDataSource(EmDataSourceType.SLAVE);
        } else {
            DataSourceHolder.setDataSource(EmDataSourceType.MASTER);
        }
        //}
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceHolder.removeDataSource();
    }

}
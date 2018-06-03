package com.zeroq6.common.datasource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

// import java.util.concurrent.ThreadLocalRandom;


public class DynamicDataSource extends AbstractRoutingDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DataSource masterDataSource;

    private List<DataSource> slaveDataSourceList;

    private Integer slaveDataSourceSize;

    public void setSlaveDataSourceList(List<DataSource> slaveDataSourceList) {
        this.slaveDataSourceList = slaveDataSourceList;
    }

    public void setMasterDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.masterDataSource == null) {
            throw new IllegalArgumentException("Property 'masterDataSource' is required");
        }
        // 默认数据源
        setDefaultTargetDataSource(masterDataSource);
        Map<Object, Object> targetDataSources = new ConcurrentHashMap<Object, Object>();
        // 存放所有数据源
        targetDataSources.put(EmDataSourceType.MASTER.name(), masterDataSource);
        if (null != slaveDataSourceList && slaveDataSourceList.size() > 0) {
            for (int i = 0; i < slaveDataSourceList.size(); i++) {
                targetDataSources.put(EmDataSourceType.SLAVE.name() + i, slaveDataSourceList.get(i));
            }
        }
        slaveDataSourceSize = null == slaveDataSourceList ? 0 : slaveDataSourceList.size();
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        EmDataSourceType dataSourceType = DataSourceHolder.getDataSource();
        if (dataSourceType == null
                || dataSourceType == EmDataSourceType.MASTER
                || null == slaveDataSourceList || slaveDataSourceList.isEmpty()) {
            return EmDataSourceType.MASTER.name();
        }
        Random random = new Random();
        //随机方式
        // int index = ThreadLocalRandom.current().nextInt(0, slaveDataSourceSize);
        int index = random.nextInt(slaveDataSourceSize);
        return EmDataSourceType.SLAVE.name() + index;
    }
}

package com.zeroq6.common.datasource;


public class DataSourceHolder {

    private static final ThreadLocal<EmDataSourceType> holder = new ThreadLocal<EmDataSourceType>();

    public static void setDataSource(EmDataSourceType dataSource){
        holder.set(dataSource);
    }

    public static EmDataSourceType getDataSource(){
        return holder.get();
    }

    public static void removeDataSource() {
        holder.remove();
    }

}
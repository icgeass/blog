package com.zeroq6.common.monitor;

/**
* 业务告警，方法可用率，心跳
* 服务器，实例
*/
/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public interface MonitorServiceApi {


    /**
     * 业务告警
     * @param key
     * @param detail
     * @throws Exception
     */
    public void bussinessAlarm(String key, String detail) throws Exception;

    /**
     * 方法开始
     * @param key
     * @throws Exception
     */
    public void begin(String key) throws Exception;


    /**
     * 方法异常
     * @param key
     * @throws Exception
     */
    public void exception(String key) throws Exception;


    /**
     * 方法正常结束
     * @param key
     * @throws Exception
     */
    public void end(String key) throws Exception;

}

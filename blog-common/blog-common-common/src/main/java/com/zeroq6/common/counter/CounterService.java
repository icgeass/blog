
package com.zeroq6.common.counter;

import com.alibaba.fastjson.JSON;

import com.zeroq6.common.cache.CacheServiceApi;
import com.zeroq6.common.counter.mybatis.PropertyParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CounterService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static ThreadLocal<List<Counter>> COUNTER_LIST_THREAD_LOCAL = new ThreadLocal<>();


    private CacheServiceApi cacheServiceApi;

    private CounterConfigMap counterConfigMap;

    public void setCounterConfigMap(CounterConfigMap counterConfigMap) {
        this.counterConfigMap = counterConfigMap;
    }

    public void setCacheServiceApi(CacheServiceApi cacheServiceApi) {
        this.cacheServiceApi = cacheServiceApi;
    }

    /**
     * 通过key获取Counter
     *
     * @param key
     * @return
     * @throws Exception
     */
    private Counter getCounter(String type, String key) throws Exception {
        counterConfigMap.assertType(type);
        String cacheKey = genCacheKey(type, key);
        String value = cacheServiceApi.get(cacheKey);
        Counter counter = null;
        // 首次获取
        if (StringUtils.isBlank(value)) {
            counter = new Counter(type, key, counterConfigMap.getMaxTimes(type), false, new Date(), null);
            // 手动更新成功或失败时才写缓存，减小系统开销
            // updateSuccess(counter); // 初始重置计数器
            return counter;
        }
        // 非首次获取
        counter = JSON.parseObject(value, Counter.class);

        // 说明counter已经不应该在缓存中了，但是缓存返回了值，说明缓存不支持每次put设置过期时间
        // 删除缓存counter并且手动设置新counter
        // 惰性删除
        if (new Date().compareTo(DateUtils.addSeconds(counter.getLastUpdateTime(), counterConfigMap.getLockSeconds(type))) > 0) {
            remove(counter);
            return new Counter(type, key, counterConfigMap.getMaxTimes(type), false, new Date(), null);
        }

        if (counter.isLock()) {
            Date now = new Date();
            if (now.compareTo(counter.getUnlockTime()) > 0) {
                counter.setUnlockTime(null);
                counter.setLock(false);
                counter.setLeftTimes(counterConfigMap.getMaxTimes(type));
                counter.setMessage(null);
                // 返回一个解锁后的counter即可，手动更新成功或失败时才写缓存，减小系统开销
                // updateSuccess(counter); // 解锁则重置计数器
            } else {
                Properties properties = getMessageProperties(counter);
                counter.setMessage(PropertyParser.parse(counterConfigMap.getMsgLock(type), properties));
            }
        }
        return counter;
    }


    public String getLockMessage(Map<String, String> type2Key) throws Exception {
        if (null == type2Key || type2Key.isEmpty()) {
            throw new RuntimeException("type2key不能为空");
        }
        List<Counter> counterList = new ArrayList<Counter>();
        String lockMessage = null;
        Integer order = null;
        for (String type : type2Key.keySet()) {
            Counter counter = getCounter(type, type2Key.get(type));
            Integer currentOrder = counterConfigMap.getOrder(counter.getType());
            counterList.add(counter);
            if (!counter.isLock()) {
                continue;
            }

            if (null == order || currentOrder < order) {
                order = currentOrder;
                lockMessage = counter.getMessage();
            }
        }
        COUNTER_LIST_THREAD_LOCAL.set(counterList);
        return lockMessage;
    }


    public String getLockMessage(String type, String key) throws Exception {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(key)) {
            throw new RuntimeException("type, key不能为空");
        }
        Map<String, String> typeKeyMap = new HashMap<>();
        typeKeyMap.put(type, key);
        return getLockMessage(typeKeyMap);
    }


    /**
     * 操作成功，重置计数
     *
     * @param counter
     * @throws Exception
     */
    private void updateSuccess(Counter counter) throws Exception {
        String type = counter.getType();
        counter.setUnlockTime(null);
        counter.setLock(false);
        counter.setLeftTimes(counterConfigMap.getMaxTimes(type));
        counter.setMessage(null);
        counter.setLastUpdateTime(new Date());
        update(counter);
    }

    private void updateSuccess(List<Counter> counterList) throws Exception {
        if (null == counterList || counterList.isEmpty()) {
            throw new RuntimeException("counterList不能为空");
        }
        for (Counter counter : counterList) {
            updateSuccess(counter);
        }
    }

    public void updateSuccess() throws Exception {
        updateSuccess(getCounterList());
        clearCounterList();
    }


    /**
     * 操作失败，减小剩余次数
     *
     * @param counter
     * @throws Exception
     */
    private String updateFailedAndGetMessage(Counter counter) throws Exception {
        String type = counter.getType();
        counter.setLeftTimes(counter.getLeftTimes() - 1);
        //
        Properties properties = null;

        if (counter.getLeftTimes() <= 0) {
            Date unlockTime = DateUtils.addSeconds(new Date(), counterConfigMap.getLockSeconds(type));
            //
            counter.setLock(true);
            counter.setUnlockTime(unlockTime);
            properties = getMessageProperties(counter);
            counter.setMessage(PropertyParser.parse(counterConfigMap.getMsgLock(type), properties));
        } else {
            properties = getMessageProperties(counter);
            counter.setMessage(PropertyParser.parse(counterConfigMap.getMsgTryFailed(type), properties));
        }
        counter.setLastUpdateTime(new Date());
        update(counter);
        return counter.getMessage();
    }

    private String updateFailedAndGetMessage(List<Counter> counterList) throws Exception {
        if (null == counterList || counterList.isEmpty()) {
            throw new RuntimeException("counterList不能为空");
        }
        String message = null;
        Integer order = null;
        for (Counter counter : counterList) {
            String currentMessage = updateFailedAndGetMessage(counter);
            Integer currentOrder = counterConfigMap.getConfig(counter.getType()).getOrder();
            if (order == null || currentOrder < order) {
                order = currentOrder;
                message = currentMessage;
            }
        }
        return message;
    }

    public String updateFailedAndGetMessage() throws Exception {
        String re = updateFailedAndGetMessage(getCounterList());
        clearCounterList();
        return re;
    }


    public static List<Counter> getCounterList() {
        return COUNTER_LIST_THREAD_LOCAL.get();
    }


    public static void clearCounterList() {
        COUNTER_LIST_THREAD_LOCAL.remove();
    }

    ////////////////////////////

    /**
     * 销毁缓存中counter
     *
     * @param counter
     * @throws Exception
     */
    private void remove(Counter counter) throws Exception {
        String type = counter.getType();
        counterConfigMap.assertType(type);
        cacheServiceApi.remove(genCacheKey(type, counter.getKey()));
    }


    private void update(Counter counter) throws Exception {
        String type = counter.getType();
        counterConfigMap.assertType(type);
        cacheServiceApi.set(genCacheKey(type, counter.getKey()), JSON.toJSONString(counter), counterConfigMap.getCacheSeconds(type));
    }


    private String genCacheKey(String type, String key) {
        return counterConfigMap.getCacheKeyPrefix(type) + type + "_" + key;
    }


    //


    private Properties getMessageProperties(Counter counter) {
        String type = counter.getType();

        Properties properties = new Properties();
        properties.put("key", counter.getKey());
        properties.put("leftTimes", String.valueOf(counter.getLeftTimes()));
        Date now = new Date();

        if (counter.getLeftTimes() <= 0) {

            Date unlockTime = counter.getUnlockTime();
            if (unlockTime == null) {
                unlockTime = DateUtils.addSeconds(now, counterConfigMap.getLockSeconds(type));
            }

            properties.put("unlockTimeAfterDesc", DateFormat.toDescBySeconds((unlockTime.getTime() - now.getTime()) / 1000));
            properties.put("unlockTime", new SimpleDateFormat(counterConfigMap.getDatePatternString(type)).format(unlockTime));
        }
        return properties;

    }

    public static class DateFormat {


        private static String[] desc = new String[]{"天", "小时", "分钟", "秒"};
        private static long[] toSeconds = new long[]{TimeUnit.DAYS.toSeconds(1), TimeUnit.HOURS.toSeconds(1), TimeUnit.MINUTES.toSeconds(1), TimeUnit.SECONDS.toSeconds(1)};


        public static String toDescBySeconds(long seconds) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < desc.length; i++) {
                if (seconds < 0L) { // 如果不需要后面的0,添加=
                    break;
                }
                long num = seconds / toSeconds[i];
                if (num != 0 || stringBuilder.length() != 0) {
                    stringBuilder.append(num + desc[i]);
                }
                seconds = seconds % toSeconds[i];
            }
            if (stringBuilder.length() == 0) {
                return "0" + desc[desc.length - 1];
            }
            return stringBuilder.toString();
        }

    }


}

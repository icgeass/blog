
package com.zeroq6.common.counter;

import com.alibaba.fastjson.JSON;

import com.zeroq6.common.cache.CacheServiceApi;
import com.zeroq6.common.utils.MyStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CounterService {

    private final Logger logger = LoggerFactory.getLogger(getClass());


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
    public Counter get(String type, String key) throws Exception {
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
        // 手动设置新counter
        if (new Date().compareTo(DateUtils.addSeconds(counter.getLastUpdateTime(), counterConfigMap.getLockSeconds(type))) > 0) {
            return new Counter(type, key, counterConfigMap.getMaxTimes(type), false, new Date(), null);
        }

        if (counter.isLock()) {
            Date now = new Date();
            if (now.compareTo(counter.getUnlockTime()) > 0) {
                counter.setUnlockTime(new Date());
                counter.setLock(false);
                counter.setLeftTimes(counterConfigMap.getMaxTimes(type));
                counter.setMessage(null);
                // 返回一个解锁后的counter即可，手动更新成功或失败时才写缓存，减小系统开销
                // updateSuccess(counter); // 解锁则重置计数器
            } else {
                counter.setMessage(MyStringUtils.format(counterConfigMap.getMsgLock(type),
                        new Object[]{counter.getKey(), new SimpleDateFormat(counterConfigMap.getDatePatternString(type)).format(counter.getUnlockTime()), DateFormat.toDescBySeconds((counter.getUnlockTime().getTime() - now.getTime()) / 1000)}));
            }
        }
        return counter;
    }


    public List<Counter> get(Map<String, String> type2Key) throws Exception {
        if (null == type2Key || type2Key.isEmpty()) {
            throw new RuntimeException("type2key不能为空");
        }
        List<Counter> re = new ArrayList<Counter>();
        for (String type : type2Key.keySet()) {
            re.add(get(type, type2Key.get(type)));
        }
        Collections.sort(re, new Comparator<Counter>() {
            @Override
            public int compare(Counter o1, Counter o2) {
                return counterConfigMap.getPriority(o1.getType()) - counterConfigMap.getPriority(o2.getType());
            }
        });
        return re;
    }

    /**
     * 操作成功，重置计数
     *
     * @param counter
     * @throws Exception
     */
    public void updateSuccess(Counter counter) throws Exception {
        String type = counter.getType();
        counter.setUnlockTime(new Date());
        counter.setLock(false);
        counter.setLeftTimes(counterConfigMap.getMaxTimes(type));
        counter.setMessage(null);
        counter.setLastUpdateTime(new Date());
        update(counter);
    }

    public void updateSuccess(List<Counter> counterList) throws Exception {
        if (null == counterList || counterList.isEmpty()) {
            throw new RuntimeException("counterList不能为空");
        }
        for (Counter counter : counterList) {
            updateSuccess(counter);
        }
    }


    /**
     * 操作失败，减小剩余次数
     *
     * @param counter
     * @throws Exception
     */
    public void updateFailed(Counter counter) throws Exception {
        String type = counter.getType();
        counter.setLeftTimes(counter.getLeftTimes() - 1);
        if (counter.getLeftTimes() <= 0) {
            Date d = DateUtils.addSeconds(new Date(), counterConfigMap.getLockSeconds(type));
            counter.setLock(true);
            counter.setUnlockTime(d);
            counter.setMessage(MyStringUtils.format(counterConfigMap.getMsgLock(type), new Object[]{counter.getKey(), new SimpleDateFormat(counterConfigMap.getDatePatternString(type)).format(d), DateFormat.toDescBySeconds(counterConfigMap.getLockSeconds(type))}));
        } else {
            counter.setMessage(MyStringUtils.format(counterConfigMap.getMsgTryFailed(type), new Object[]{counter.getKey(), counter.getLeftTimes()}));
        }
        counter.setLastUpdateTime(new Date());
        update(counter);
    }

    public void updateFailed(List<Counter> counterList) throws Exception {
        if (null == counterList || counterList.isEmpty()) {
            throw new RuntimeException("counterList不能为空");
        }
        for (Counter counter : counterList) {
            updateFailed(counter);
        }
    }


    /**
     * 销毁缓存中counter
     *
     * @param counter
     * @throws Exception
     */
    public void remove(Counter counter) throws Exception {
        String type = counter.getType();
        counterConfigMap.assertType(type);
        cacheServiceApi.remove(genCacheKey(type, counter.getKey()));
    }

    public String getMessage(List<Counter> counterList) throws Exception {
        if (null == counterList || counterList.isEmpty()) {
            return null;
        }
        Collections.sort(counterList, new Comparator<Counter>() {
            @Override
            public int compare(Counter o1, Counter o2) {
                return counterConfigMap.getPriority(o1.getType()) - counterConfigMap.getPriority(o2.getType());
            }
        });
        return counterList.get(0).getMessage();
    }

    ////////////////////////////


    private void update(Counter counter) throws Exception {
        String type = counter.getType();
        counterConfigMap.assertType(type);
        cacheServiceApi.set(genCacheKey(type, counter.getKey()), JSON.toJSONString(counter), counterConfigMap.getCacheSeconds(type));
    }


    private String genCacheKey(String type, String key) {
        return counterConfigMap.getCacheKeyPrefix(type) + type + "_" + key;
    }


    //

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
import com.zeroq6.common.cache.RedisCacheService;
import com.zeroq6.common.security.AesCrypt;
import com.zeroq6.common.security.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class NoSpringTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Test
    public void test() throws Exception {
        final RedisCacheService redisService = new RedisCacheService();
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        redisService.setHost("127.0.0.1");
        redisService.setPort(6379);
        redisService.setMinIdle(1);
        redisService.setMaxIdle(1);
        redisService.setMaxTotal(3);
        redisService.setMaxWaitMillis(1000);
        redisService.afterPropertiesSet();
        Thread targetThread = null;
        for (int i = 0; i < 5; i++) {
            targetThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        redisService.set(atomicInteger.getAndIncrement() + "", System.currentTimeMillis() + "");
                    } catch (Exception e) {
                        logger.info(e.getMessage(), e);
                    }
                }
            });
            targetThread.start();
//            targetThread.join();
            System.out.println("started: " + i);

        }
//        targetThread.join();
        Thread.currentThread().sleep(5000);
        System.out.println("ok");
    }

}

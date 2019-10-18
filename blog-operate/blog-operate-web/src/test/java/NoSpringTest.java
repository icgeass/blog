
import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.counter.CounterService;
import com.zeroq6.common.counter.mybatis.PropertyParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class NoSpringTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Test
    public void testSha1Gen() throws Exception {

        System.out.printf(DigestUtils.sha1Hex(LoginService.PASS_SLAT + "changeit"));


    }

    @Test
    public void testToDesc() throws Exception {
        System.out.println(CounterService.DateFormat.toDescBySeconds(60 * 60 * 24));
        System.out.println(CounterService.DateFormat.toDescBySeconds(60 * 60 * 24 + 1));
        System.out.println(CounterService.DateFormat.toDescBySeconds(61));
        System.out.println(CounterService.DateFormat.toDescBySeconds(1));
        System.out.println(CounterService.DateFormat.toDescBySeconds(60));
        System.out.println(CounterService.DateFormat.toDescBySeconds(60 * 60));
        System.out.println(CounterService.DateFormat.toDescBySeconds(-1));
        System.out.println(CounterService.DateFormat.toDescBySeconds(0));
        System.out.println(CounterService.DateFormat.toDescBySeconds(60 * 60 + 60 * 59 + 1));




    }


    @Test
    public void testProp() throws Exception {
        Properties properties = new Properties();
        properties.put("a", "aaa");
        properties.put("b", 1);
        System.out.println(properties.containsKey("a"));
        System.out.println(properties.getProperty("a"));
        System.out.println(properties.containsKey("b"));
        System.out.println(properties.getProperty("b"));


        System.out.println(PropertyParser.parse("a=@{a},b=@{b}", properties));

    }


}

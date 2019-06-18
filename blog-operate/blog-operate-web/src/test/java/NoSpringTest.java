
import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.counter.CounterService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


}

import com.zeroq6.common.security.AesCrypt;
import com.zeroq6.common.security.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoSpringTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Test
    public void test(){
        int i = -1;
        System.out.println(i);


        String s = "vgfltl051Dxwn1xDFxvbS3ijgWcL9w1fbUIiwoZDuTJZiBZuWgBpJ_vhfr7vTWIM";
        AesCrypt aesCrypt = new AesCrypt("8W0WABRNDQYQYD4EVEMUKVYBFBF4JMRJP3I7C89XR4KI9UK79F1GOM0BHN94VPLM", 256);
        String dec = aesCrypt.decrypt(Base64.getUrlDecoder().decode(s));
        System.out.println(dec);
    }

}

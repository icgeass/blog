
import com.alibaba.fastjson.JSON;
import com.zeroq6.blog.operate.domain.BackupConfigDomain;
import com.zeroq6.blog.operate.service.login.LoginService;
import com.zeroq6.common.mail.MailSenderConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class NoSpringTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {

        System.out.printf(DigestUtils.sha1Hex(LoginService.PASS_SLAT + "changeit"));


    }


}

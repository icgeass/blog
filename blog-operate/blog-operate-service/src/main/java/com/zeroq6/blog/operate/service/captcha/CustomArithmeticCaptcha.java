package com.zeroq6.blog.operate.service.captcha;

import com.wf.captcha.ArithmeticCaptcha;

public class CustomArithmeticCaptcha extends ArithmeticCaptcha {

    @Override
    protected char[] alphas() {
        if (num(4) < 3) {
            return super.alphas();
        } else {
            StringBuilder sb = new StringBuilder();
            int a = num(10);
            int b = num(1, 10);
            int c = a * b;
            sb.append(c).append("/").append(b).append("=?");
            chars = String.valueOf(a);
            super.setArithmeticString(sb.toString());
            return chars.toCharArray();
        }
    }
}

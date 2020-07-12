package com.zeroq6.blog.operate.service.captcha;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zeroq6.common.cache.CacheServiceApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class CaptchaService {

    private final List<Integer> captchaFontList = new ArrayList<Integer>() {
        {
            add(Captcha.FONT_1);
            add(Captcha.FONT_2);
        }
    };

    private final Random random = new Random();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String timesKey = "times";

    private final String valueKey = "value";


    @Autowired
    private CacheServiceApi cacheServiceApi;


    public JSONObject getNewCaptcha() {
        JSONObject jsonObject = new JSONObject();
        try {
            int randType = random.nextInt(2);
            int randFont = captchaFontList.get(random.nextInt(captchaFontList.size()));
            String captchaKey = UUID.randomUUID().toString().replace("-", "");
            String captchaImageBase64 = null;
            String captchaValue = null;
            if (randType == 0) {
                SpecCaptcha captcha = new SpecCaptcha();
                captcha.setLen(4);
                captcha.setFont(randFont);
                captchaImageBase64 = captcha.toBase64();
                captchaValue = captcha.text();
            } else {
                ArithmeticCaptcha captcha = new ArithmeticCaptcha();
                captcha.setFont(randFont);
                captchaImageBase64 = captcha.toBase64();
                captchaValue = captcha.text();
            }
            jsonObject.put("success", true);
            jsonObject.put("captchaKey", captchaKey);
            jsonObject.put("captchaImageBase64", captchaImageBase64);
            JSONObject captchaValueJson = new JSONObject();
            captchaValueJson.put(valueKey, captchaValue);
            captchaValueJson.put(timesKey, 0);
            cacheServiceApi.set(captchaKey, captchaValueJson.toJSONString());
        } catch (Exception e) {
            jsonObject.put("success", false);
            logger.error("getNewCaptcha error", e);
        }
        return jsonObject;
    }

    public boolean validate(String captchaKey, String captchaValue) {
        try {
            if (StringUtils.isBlank(captchaKey) || StringUtils.isBlank(captchaValue)) {
                return false;
            }
            if ("null".equals(captchaKey) || "null".equals(captchaValue)) {
                return false;
            }
            String rightValueStr = cacheServiceApi.get(captchaKey);
            if (StringUtils.isBlank(rightValueStr)) {
                return false;
            }
            JSONObject jsonValue = JSON.parseObject(rightValueStr);
            int times = jsonValue.getIntValue(timesKey);
            if (times > 5) {
                cacheServiceApi.remove(captchaKey);
                return false;
            }
            boolean success = jsonValue.getString(valueKey).equalsIgnoreCase(captchaValue);
            if (!success) {
                cacheServiceApi.remove(captchaKey);
                logger.error("验证码错误，captchaKey={}， captchaValue={}，rightValue={}", captchaKey, captchaValue, rightValueStr);
            } else {
                jsonValue.put(timesKey, ++times);
                cacheServiceApi.set(captchaKey, jsonValue.toJSONString());
            }
            return success;
        } catch (Exception e) {
            logger.error("validate error, captchaKey={}, captchaValue={}", captchaKey, captchaValue, e);
            return false;
        }
    }


}
